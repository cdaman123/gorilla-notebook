(ns pinkgorilla.events.settings
  "events related to the settings dialog"
  (:require
   [taoensso.timbre :refer-macros (info)]
   [cljs.reader :as reader] ; local storage parsing
   [re-frame.core :as re-frame :refer [reg-event-db reg-event-fx path trim-v after debug dispatch dispatch-sync]]
   ;[pinkgorilla.events.helper :refer [text-matches-re default-error-handler  check-and-throw  standard-interceptors]]
   ))

;; LocalStorage Helpers

(defn ls-set! [k v]
  (.setItem js/localStorage (pr-str k) (pr-str v)))

(defn ls-get [k]
  (when-let [s (.getItem js/localStorage (pr-str k))]
    (reader/read-string s)))

(defn ls-remove! [k]
  (.removeItem js/localStorage k))


;; Settings Dialog Visibility

(reg-event-db
 :app:showsettings
 (fn [db [_ doc]]
   (assoc-in db [:dialog :settings] true)))

(reg-event-db
 :app:hide-settings
 (fn [db _]
   (dispatch [:settings-localstorage-save]) ; save to localstorage on close of dialog.
   (assoc-in db [:dialog :settings] false)))

;; Settings Change

(reg-event-db
 :settings-localstorage-load
 (fn [db [_]]
   (let [settings (ls-get :notebook-settings)]
     (if (nil? settings)
       (do (info "localstorage does not contain settings.")
           db)
       (do (info "loaded settings from localstorage: "  settings)
           (assoc-in db [:settings] settings))))))

(reg-event-db
 :settings-localstorage-save
 (fn [db [_]]
   (let [settings (:settings db)]
     (ls-set! :notebook-settings settings)
     (info "localstorage settings saved: " settings)
     db)))


(reg-event-db
 :settings-set
 (fn [db [_ k v]]
   (info "changing setting " k " to: " v)
   (assoc-in db [:settings k] v)))

