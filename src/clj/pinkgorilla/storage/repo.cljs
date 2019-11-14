(ns pinkgorilla.storage.repo
  (:require
   [taoensso.timbre :refer-macros (info)]
   [goog.crypt.base64 :as b64]
   [pinkgorilla.storage.storage :refer [Storage query-params-to-storage]]))


(defrecord StorageRepo [user repo path])

(defmethod query-params-to-storage :repo [_ params]
  (StorageRepo.
   (:user params)
   (:repo params)
   (:path params)))


(extend-type StorageRepo
  Storage
  (load-url [self  base-path]
    (info "repo-storage.load-url")
    (str "https://api.github.com/repos/" (:user self) "/" (:repo self) "/contents/" (:path self)))

  (decode-content [self response]
    (b64/decodeString (get response "content")))


  (save [self]
    (info "repo-storage.save"))

  (external-url [self]
    (info "repo-storage.external-url")
    nil)

  (gorilla-path [self]
    (info "repo-storage.gorilla-path")
    (str "/edit?worksheet-filename=" (:id self))))



