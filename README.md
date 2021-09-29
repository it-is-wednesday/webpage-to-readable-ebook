# web-epub

## Prerequisites

Leiningen for project management: https://github.com/technomancy/leiningen

These programs need to be available on PATH with these exact names on runtime:
- pandoc: https://pandoc.org/installing.html
- ebook-convert: https://calibre-ebook.com/download
- wkhtmltopdf: https://wkhtmltopdf.org/downloads.html

## Running

To start a web server for the application, run:

    lein ring server-headless

## Building JAR file

    lein ring uberjar
