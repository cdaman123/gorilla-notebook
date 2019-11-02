(ns gorilla-repl.output.jsscript
  (:require
   [reagent.core :as reagent :refer [atom]]
   [re-frame.core :refer [subscribe dispatch dispatch-sync]]
   [cljs-uuid-utils.core :as uuid]
   ))

;; SCRIPT INJECTION could be done by adding script-elements to the dom with addChild,
;; But we currently use RequireJS to load modules from third party components.

; this is the container of loaded scripts
; (def jsscript-container (reagent/atom {}))
; (def jsscript-root (.getElementById js/document "jsscript-root"))
  
; (defn inject-script 
;  "inject a javascript snippet to the dom
;   Does not keep track of which scripts were loaded or not."
;  [javascript-snippet]
;  (let [script (.createElement js/document "script")]
;    (.setAttribute script "type" "text/javascript")
;    ;(.setAttribute script "src" "helper.js")
;    (set! (.-innerHTML script) javascript-snippet)
;    (.appendChild jsscript-root script)
;    ))
  

(defn execute-render [id-or-el data module-js]
  (let [;_ (println "executing script!")
        module (js->clj module-js :keywordize-keys true)
        data-js (clj->js data)
        ;_ (println "module:" module)
        render-fn (:render module)
        version (:version module)
        ]
    ;(println render-fn)
    (println "rendering version " version)
    (render-fn id-or-el data-js)
    ;(println "rendering: " (render-fn id data))
    ))

;; This works:
;; (.require js/window (clj->js ["demo"]) #(println "result: " %))
;; (run-script "define([],function(){return 'world!'})")
;; (run-script "andreas" "define([],function(){return {render: function (name) {return 'hello, ' + name}}})")


(defn run-script [id-or-el data javascript-snippet]
  (let [module (str "loadstring!" javascript-snippet)
        ;module "demo"
        modules-js (clj->js [module])
        ;_ (println "js module source: " modules-js)
        ]
    (.require js/window modules-js (partial execute-render id-or-el data))
    ;x (js/require modules-js #(println "rcvd2: " %))    ; it should also work this way.
    nil ; suppress returning the requirejs module definition
    ))



(defn output-jsscript
  [output _]
  (let [uuid (uuid/uuid-string (uuid/make-random-uuid))
        ;div-uuid (keyword (str "div#" uuid))
        content (:content output)
        ;_ (println "content: " content)
        snippet (:module content)
        data (:data content)]
        (reagent/create-class 
         {:display-name "output-jsscript"
          :reagent-render (fn [] [:div {:id uuid}])
          :component-did-mount (fn [this]
                                 ;(run-script uuid data snippet)
                                 (run-script (reagent/dom-node this) data snippet)
                                 )
          ;:component-did-update (fn [this]
          ;                        (run-script uuid data snippet))
          
          :component-will-update (fn [this [_ new-spec]]
                                   ;(run-script uuid data snippet)
                                   (run-script (reagent/dom-node this) data snippet)
                                   )
                                   ;(render-vega new-spec (r/dom-node this)))
          
          })))


 