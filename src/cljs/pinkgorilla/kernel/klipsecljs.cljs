(ns pinkgorilla.kernel.klipsecljs
  (:require-macros
   [gadjett.core :refer [dbg]]
   [cljs.core.async.macros :refer [go go-loop]])
  (:require
   [klipse-clj.lang.clojure :as cklipse]
   klipse-clj.lang.clojure.bundled-namespaces
   pinkgorilla.kernel.bundled-dependencies
   [pinkgorilla.kernel.cljs-tools :refer [r!]]
   [gadjett.core-fn]
   [cljs.tagged-literals :as tags]
   [goog.dom :as gdom]
   [clojure.string :refer [blank?]]
   [klipse-clj.repl :refer [get-completions current-alias-map st create-state-compile current-ns-eval current-ns-compile reset-ns-eval! reset-ns-compile!]]
   [klipse-clj.lang.clojure.guard :refer [min-max-eval-duration my-emits watchdog]]
   [klipse-clj.lang.clojure.io :as io]
   [clojure.pprint :as pprint]
   [cljs.analyzer :as ana]
   [klipse-clj.lang.cljs-repl :refer [error->str]] ;; once error->str is in cljs, take it from there
   [cljs.tools.reader :as r]
   [cljs.tools.reader.reader-types :as rt]
   [clojure.string :as s]
   [cljs.compiler :as compiler]
   [cljs.core.async :refer [timeout chan close! put! <!]]
   [cljs.env :as env]
   [cljs.js :as cljs]

   [clojure.string :as str]
   [cljs-uuid-utils.core :as uuid]
   [re-frame.core :refer [dispatch]]
   [taoensso.timbre :refer-macros (info)]

   [pinkgorilla.ui.gorilla-renderable :refer [render]]
   [pinkgorilla.ui.rendererCLJS]
   [quil.core :include-macros true]  ; awb99: cannot do this in bundled-dependencies it seems.

   ))

(defn init-klipse! []
  (go (<! (cklipse/create-state-eval))
      (info "klipse init done")))


(defn send-console [segment-id result]
  (dispatch [:evaluator:console-response segment-id {:console-response result}]))

(defn send-value [segment-id result]
  (dispatch
   [:evaluator:value-response
    segment-id
    result
    'cljs.user]))

(defn render-embedded [result]
  (let [s (pr-str result)]
    {:value-response
     {:type "html"
      :content ["span" s]
      :value s}}))

(defn render-renderable [result]
  (let [response   {:value-response (render result)}
        _ (println "response: " response)
        ]
    response))

;; PREPL

;; result is in this form:
#_{:tag :ret
   :ns cljs.user
   :val "14↵"
   :form "(+ 7 7)"}

(defn send-result-prepl [segment-id result]
  (info "cljs eval-prepl result:" result)
  (send-console segment-id (:val result)))

(defn send-eval-message-old!
  [segment-id snippet]
  (do
    (send-console segment-id "cljs eval-prepl started..")
    (go (send-result-prepl segment-id (<! (cklipse/eval-async-prepl snippet {}))))) 0)

;; EVAL

;; result:
;; [:ok value]
;; [:error #error {:message "ERROR", :data {:tag :cljs/analysis-error}, :cause #object[TypeError TypeError: bongo.trott.g is undefined]}]
(defn send-result-eval [segment-id result]
  (info "cljs eval result:" result)
  (send-console segment-id (str (pr-str result) " type: " (type (nth result 1))))
  (send-value segment-id (render-renderable (nth result 1)))
  (dispatch [:evaluator:done-response segment-id])) ; assumption: only one response to eval

(defn send-eval-message!
  [segment-id snippet]
  (do
    (send-console segment-id "cljs eval started..")
    (go (send-result-eval segment-id (<! (cklipse/the-eval snippet {}))))
    nil))
