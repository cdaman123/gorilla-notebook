(ns pinkgorilla.events.storage
  (:require
   [re-frame.core :as re-frame :refer [reg-event-db reg-event-fx path trim-v after debug dispatch dispatch-sync]]
   [ajax.core :as ajax :refer [GET POST]]
   [taoensso.timbre :refer-macros (info)]
   [pinkgorilla.notebook.core :refer [load-notebook-hydrated]]
   [pinkgorilla.storage.storage :refer [Storage query-params-to-storage load-url decode-content]]
   [pinkgorilla.routes :as routes]
   [pinkgorilla.events.helper :refer [text-matches-re default-error-handler  check-and-throw  standard-interceptors]]))


(reg-event-fx                                               ;; note the trailing -fx
 :view-file
 (fn [{:keys [db]} [_ params]]
   (let [source (:source params)
         storage-type (case source
                        "local" :local
                        "gist" :gist
                        "repo" :repo
                        "bitbucket" :bitbucket 
                        :default)
         storage (query-params-to-storage storage-type params)
         url (load-url storage (:base-path db))
         _ (info "loading from storage:" storage-type " url: " url)]
     {:db         db
      :http-xhrio {:method          :get
                   :uri             url
                   :timeout         5000
                   :response-format (ajax/json-response-format)
                   :on-success      [:process-load-file-response storage]
                   :on-failure      [:process-error-response]}})))



(reg-event-fx                                               ;; note the trailing -fx
 :edit-file
 (fn [{:keys [db]} [_ params]]
   (let [_ (info "edit-file params:" params)
         source (:source params)
         storage-type (case source
                        "local" :local
                        "gist" :gist
                        "repo" :repo
                        "bitbucket" :bitbucket 
                        :default)
         storage (query-params-to-storage storage-type params)
         url (load-url storage (:base-path db))
         _ (info "loading from storage:" storage-type " url: " url)]
   {:db         (update-in db [:save] dissoc :saved)
    :http-xhrio {:method          :get
                 :uri             url
                 :timeout         5000
                 :response-format (ajax/json-response-format)
                 :on-success      [:process-load-file-response storage]
                 :on-failure      [:process-error-response]}})))



(reg-event-db
 :process-load-file-response
 [standard-interceptors]
 (fn
   [db [_ storage content]]
   (let [content (decode-content storage content)
         _ (info "Content loaded:\n" content)
         notebook (load-notebook-hydrated content)
         _ (info "notebook: " notebook )]
     (assoc db
            :worksheet notebook
            :storage storage))))
