(ns web-epub.handler
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [selmer.parser :as selmer]
            [clj-http.client :as client]
            [clojure.java.shell :refer [sh]]
            [clojure.java.io :as io])
  (:import [net.dankito.readability4j Readability4J]))

(selmer/set-resource-path! "public")

(defn readable-html [dirty-html url]
  (.. (new Readability4J url dirty-html) parse getContentWithUtf8Encoding))

(defn html->epub [html]
  (sh "pandoc" "--from" "html" "--to" "epub" :in html :out-enc :bytes))

(defroutes app-routes
  (GET "/" [] (selmer/render-file "index.html" {:name "hello"}))
  (GET "/webpage.epub" [url]
    (-> url client/get :body (readable-html url) html->epub :out io/input-stream))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))

(comment
  (selmer/render-file "index.html" {:name "hello"})

  (html->epub "<html>a</html>"))
