(ns pinkgorilla.storage.gist
  (:require
   [taoensso.timbre :refer-macros (info)]
   [pinkgorilla.storage.storage :refer [Storage query-params-to-storage]]))


(defrecord StorageGist [id filename user])

(defmethod query-params-to-storage :gist [_ params]
  (info "gist params: " params)
  (StorageGist.
   (:id params)
   (:filename params)
   (:user params)))


(extend-type StorageGist
  Storage

  (load-url [self  base-path]
    (info "gist-storage.load-url" self)
            ;https://api.github.com/gists/55b101d84d9b3814c46a4f9fbadcf2f8
    (str "https://api.github.com/gists/" (:id self)))



  (decode-content [self response]
    (let [files (get response "files")
          name (if (= 1 (count files))
                 (first (keys files))
                 (:filename self))
          content (-> (get files name)
                      (get "content"))]
      content))

  (save
    [self]
    (info "gist-storage.save"))

  (external-url [self]
    (info "local-storage.external-url")
    ;https://gist.github.com/awb99/55b101d84d9b3814c46a4f9fbadcf2f8
    (str "https://gist.github.com/" (:user self) "/" (:id self)))

  (gorilla-path [self]
    (info "local-storage.gorilla-path")
    (str "/edit?worksheet-filename=" (:id self))))



