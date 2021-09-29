(defproject web-epub "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [javax.servlet/servlet-api "2.5"]
                 [net.dankito.readability4j/readability4j "1.0.3"]
                 [selmer "1.12.44"]
                 [clj-http "3.12.3"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler web-epub.handler/app
         :nrepl {:start? true :port 20612}}
  :profiles {:dev {:dependencies [[ring/ring-mock "0.3.2"]]}})
