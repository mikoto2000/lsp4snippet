lsp4snippet
===========

Language server for snippets.


Features:
---------

- [ ] : textDocument/completion.
    - [ ] : load snippet from YAML file.
        - [x] : single YAML file support.
        - [x] : multi YAML file support.
        - [ ] : glob support.
    - [x] : insert indent
- [ ] : file extension to file type mapping mechanism
- [ ] : incremental synchronization


Usage:
------

```sh
java --add-modules=ALL-SYSTEM --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED -Declipse.application=org.eclipse.jdt.ls.core.id1 -Dosgi.bundles.defaultStartLevel=4 -Declipse.product=org.eclipse.jdt.ls.core.product -Dlog.level=ALL -noverify -Dfile.encoding=UTF-8 -Xmx1G -jar /PATH/TO/lsp4snippet-x.y.z.jar --snippet /PATH/TO/Configuration.yaml
```


Requirements:
-------------

- OpenJDK 12 or higher


Configuration:
--------------

example:

```yaml
snippets:
    javascript:
        - label: func
          description: function
          newText: |-
              function ${1:name}(${2:args}) {
                  ${3:content}
              }
        - label: if
          description: if statement
          newText: |-
              if (${1:condition}) {
                  ${2:expr}
              }
        - label: for
          description: for statement
          newText: |-
              for (${1:initial} : ${2:condition} : ${3:increment}) {
                  ${4:expr}
              }
```

License:
--------

```
Copyright (c) 2019 mikoto2000

This program and the accompanying materials are made
available under the terms of the Eclipse Public License 2.0
which is available at https://www.eclipse.org/legal/epl-2.0/
```


Author:
-------

mikoto2000 <mikoto2000@gmail.com>

