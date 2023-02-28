# web-ebook

## Prerequisites

Leiningen for project management: https://github.com/technomancy/leiningen

These programs need to be available on PATH with these exact names on runtime:
- pandoc: https://pandoc.org/installing.html
- ebook-convert: https://calibre-ebook.com/download
- wkhtmltopdf: https://wkhtmltopdf.org/downloads.html

## Development

To start a hot-reloading web server for the application, run:
``` shell
lein ring server-headless
```

## Deployment

Deploying a jar is probably the most straightforward:
``` shell
lein ring uberjar
```

A containerized solution is also an option, however keep in mind all of this project's dependencies are quite huge, and the container yielded is around 2GB

## Credits

[Readability4J](https://github.com/dankito/Readability4J) is doing all the heavy lifting here. Go
star it
