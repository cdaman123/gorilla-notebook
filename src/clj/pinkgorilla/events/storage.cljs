(ns pinkgorilla.events.storage
  (:require
   [re-frame.core :as re-frame :refer [reg-event-db reg-event-fx path trim-v after debug dispatch dispatch-sync]]
   [ajax.core :as ajax :refer [GET POST]]
   [taoensso.timbre :refer-macros (info)]
   [pinkgorilla.notebook.core :refer [load-notebook-hydrated save-notebook-hydrated]]
   [pinkgorilla.storage.storage :refer [Storage query-params-to-storage load-url decode-content save-url encode-content]]
   [pinkgorilla.events.helper :refer [text-matches-re default-error-handler  check-and-throw  standard-interceptors]]))


;; Load File (from URL Parameters) - in view or edit mode

;; http://localhost:3449/worksheet.html#/view?source=github&user=JonyEpsilon&repo=gorilla-test&path=ws/graph-examples.clj
;; http://localhost:3449/worksheet.html#/view?source=gist&id=2c210794185e9d8c0c80564db581b136&filename=new-render.clj


(reg-event-fx
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


(reg-event-fx 
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

;; SAVE File

(reg-event-db
 :app:save
 [standard-interceptors]
 (fn [db _]
   (if-let [storage (get-in db [:storage])]
     (dispatch [:save-storage storage]) ; just save to storage if we have a storage
     (dispatch [:app:saveas])) ;otherwise open save dialog 
   db))


(reg-event-fx
 :save-storage
 (fn [{:keys [db]} [_ storage]]
    (let [notebook (save-notebook-hydrated (:worksheet db))
          url (save-url storage (:base-path db))
          _ (info "saving to storage " url)
          content (encode-content storage notebook)]
   {:db         (assoc-in db [:save :show] false)
    :http-xhrio {:method          :post
                 :body           content 
                 :uri            url
                 :timeout         5000                     ;; optional see API docs
                 :response-format (ajax/transit-response-format) ;; IMPORTANT!: You must provide this.
                 :on-success      [:after-save-success storage]
                 :on-failure      [:process-error-response]}})))


(reg-event-db
 :after-save-success
 [standard-interceptors]
 (fn [db [_ storage]]
   (dispatch [:display-message (str "saved.") 2000])
   ;(routes/nav! (str "/edit?source=local&filename=" filename))
   ;(assoc-in db [:save :saved] true)
   db
   ))


