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

(defn html->epub [title html]
  (:out (sh "pandoc"
            "--standalone"
            "--from" "html"
            "--to" "epub"
            "--metadata" (str "title=" title)
            :in html
            :out-enc :bytes)))

(defn html->mobi [html]
  (let [infile-path (.getPath (File/createTempFile "html" ".html"))
        outfile (File/createTempFile "book" ".mobi")]
    (spit infile-path html)
    (sh "ebook-convert" infile-path (.getPath outfile))
    outfile))

(defn html->pdf
  "Convert to PDF via wkhtmltopdf.

  If both pdf-width and pdf-height are not an empty string, they will be passed as
  --page-width and --page-height to the wkhtmltopdf command.

  Returns a java.io.File object"
 [pdf-width pdf-height html]
  (let [infile-path (.getPath (File/createTempFile "html" ".html"))
        outfile (File/createTempFile "book" ".pdf")
        cmd (concat ["wkhtmltopdf"]
                    (if (and (not-empty pdf-width) (not-empty pdf-height))
                      ["--page-width" pdf-width "--page-height" pdf-height]
                      [])
                    [infile-path (.getPath outfile)])]
    (spit infile-path html)
    (apply sh (vec cmd))
    outfile))

(defroutes app-routes
  (GET "/" [] (selmer/render-file "index.html" {}))
  (GET "/webpage" [url output-format pdf-width pdf-height]
    (let [readable (-> url client/get :body (make-readable url))
          conversion-fn (case output-format
                          "epub" (partial html->epub (.getTitle readable))
                          "mobi" html->mobi
                          "pdf"  (partial html->pdf pdf-width pdf-height))
          ebook-bytes (-> readable
                          .getContentWithUtf8Encoding
                          conversion-fn
                          io/input-stream)
          title (str (.getTitle readable) "." output-format)]
      {:headers {"Content-Disposition" (format "attachment; filename=\"%s\"" title)}
       :body ebook-bytes}))
  (route/resources "/static")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
