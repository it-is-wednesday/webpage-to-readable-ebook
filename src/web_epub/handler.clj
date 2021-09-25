(ns web-epub.handler
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [selmer.parser :as selmer]
            [clj-http.client :as client]
            [clojure.java.shell :refer [sh]]
            [clojure.java.io :as io])
  (:import [net.dankito.readability4j Readability4J]
           [java.io File]))

(selmer/set-resource-path! "public")

(defn make-readable [dirty-html url]
  (.parse (new Readability4J url dirty-html)))

(defn html->epub [html]
  (:out (sh "pandoc" "--from" "html" "--to" "epub" :in html :out-enc :bytes)))

(defn html->mobi [html]
  (let [infile-path (.getPath (File/createTempFile "html" ".html"))
        outfile (File/createTempFile "book" ".mobi")]
    (spit infile-path html)
    (sh "ebook-convert" infile-path (.getPath outfile))
    outfile))

(defn html->pdf [html]
  (let [infile-path (.getPath (File/createTempFile "html" ".html"))
        outfile (File/createTempFile "book" ".pdf")]
    (spit infile-path html)
    (sh "wkhtmltopdf" infile-path (.getPath outfile))
    outfile))

(defroutes app-routes
  (GET "/" [] (selmer/render-file "index.html" {:name "hello"}))
  (GET "/webpage" [url output-format]
    (let [conversion-fn (case output-format
                          "epub" html->epub
                          "mobi" html->mobi
                          "pdf"  html->pdf)
          readable (-> url client/get :body (make-readable url))
          ebook-bytes (-> readable
                          .getContentWithUtf8Encoding
                          conversion-fn
                          io/input-stream)
          title (str (.getTitle readable) "." output-format)]
      {:headers {"Content-Disposition" (format "attachment; filename=\"%s\"" title)}
       :body ebook-bytes}))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
