(ns pinkgorilla.storage.bitbucket
  (:require
   [taoensso.timbre :refer-macros (info)]
   [pinkgorilla.storage.storage :refer [Storage query-params-to-storage]]))


(defrecord StorageBitbucket [user repo revision path])


(defmethod query-params-to-storage :bitbucket [_ params]
  (StorageBitbucket. 
    (:user params)
    (:repo params)
    (or (:revision params) "HEAD")
    (:path params)
))


(extend-type StorageBitbucket
  Storage
  
  (load-url [self base-path]
    (info "bitbucket.load")
    (str "https://bitbucket.org/api/1.0/repositories/" (:user self) "/" (:repo self) "/raw/" (:revision self)"/" (:path self)))
  
  (save [self]
    (info "bitbucket.save"))
  
  (external-url [self]
    (info "local-storage.external-url")
    nil)
  
  (gorilla-path
    [self]
    (info "bitbucket.gorilla-path")
    (str "/edit?worksheet-filename=" (:user self))))




