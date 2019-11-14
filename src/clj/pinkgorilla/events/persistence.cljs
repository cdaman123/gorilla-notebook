(ns pinkgorilla.events.persistence
  (:require
   [goog.crypt.base64 :as b64]
   [re-frame.core :as re-frame :refer [reg-event-db reg-event-fx path trim-v after debug dispatch dispatch-sync]]
   [ajax.core :as ajax :refer [GET POST]]
   [taoensso.timbre :refer-macros (info)]
   [pinkgorilla.notebook.core :refer [load-notebook-hydrated]]
   [pinkgorilla.routes :as routes]
   [pinkgorilla.events.helper :refer [text-matches-re default-error-handler  check-and-throw  standard-interceptors]]))





(reg-event-db
 :process-files-response
 [standard-interceptors]
 (fn
   [db [_ response]]
   (let [file-items (->> (:files response)
                         (map (fn [x] {:text    x
                                       :desc    (str "<div class=\"command\">" x "</div>")
                                       ;; For now, we have to take/return db due to clojuredocs sync window.open
                                       :handler (fn [db]
                                                  (routes/nav! (str "/edit?source=local&filename=" x))
                                                  db)})))
         palette (:palette db)]
     (assoc-in db [:palette] (merge palette {:all-items     file-items
                                             :visible-items file-items
                                             :show          true
                                             :label         "Choose a file to load:"})))))






