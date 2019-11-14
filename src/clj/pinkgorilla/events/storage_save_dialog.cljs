(ns pinkgorilla.events.storage-save-dialog
  (:require
   [ajax.core :as ajax :refer [GET POST]]
   [re-frame.core :as re-frame :refer [reg-event-db reg-event-fx path trim-v after debug dispatch dispatch-sync]]
   [pinkgorilla.db :as db :refer [initial-db]]
   [pinkgorilla.notebook.core :refer [save-notebook-hydrated]]
   [pinkgorilla.routes :as routes]
   [pinkgorilla.events.helper :refer [text-matches-re default-error-handler  check-and-throw  standard-interceptors]]))


(reg-event-db
 :app:saveas
 [standard-interceptors]
 (fn [db _]
   (assoc-in db [:save :show] true)))


(reg-event-db
 :save-dialog-cancel
 [standard-interceptors]
 (fn [db _]
   (assoc-in db [:save] {:show false})))


(reg-event-db
 :save-dialog-keydown
 (fn [db [_ keycode]]
   (case keycode
     27 (do                                                ;; esc
          (dispatch [:save-dialog-cancel])
          db)
     13 (do                                                ;; Enter
          (dispatch [:save-file (get-in db [:save :filename])])
          db)
     db)))

(reg-event-db
 :save-dialog-change
 [standard-interceptors]
 (fn [db [_ value]]
   (assoc-in db [:save :filename] value)))






