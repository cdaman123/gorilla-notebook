(ns pinkgorilla.storage.local
  (:require
   [taoensso.timbre :refer-macros (info)]
   [pinkgorilla.storage.storage :refer [Storage query-params-to-storage]]))


(defrecord StorageLocal [filename])

(defmethod query-params-to-storage :local [_ params]
  (StorageLocal. (:filename params)))



(extend-type StorageLocal
  Storage
  (load-url [self base-path]
    (info "local-storage.load")
    (str base-path "load?worksheet-filename=" (js/encodeURIComponent (:filename self))))

  (decode-content [self content]
    (get content "worksheet-data"))

  (save-url [self base-path]
    (info "local-storage.save-url")
    (str base-path "save")
            ;(str base-path "save?worksheet-filename=" (js/encodeURIComponent (:filename self)))
    )

  (encode-content [self notebook]
    (info "local-storage.encode-content")
    (str "worksheet-filename=" (js/encodeURIComponent (:filename self))
         "&worksheet-data=" (js/encodeURIComponent notebook)))

  (external-url [self]
    (info "local-storage.external-url")
    nil)

  (gorilla-path [self]
    (info "local-storage.gorilla-path")
    (str "/edit?worksheet-filename=" (:filename self))))



