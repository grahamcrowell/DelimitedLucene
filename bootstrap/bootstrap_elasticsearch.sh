#!/usr/bin/env bash

function start_if_installed {
    which elasticsearch
    if [ "${?}" = "0" ]; then
        printf "elasticsearch installed\n";
#        elasticsearch -Epath.conf=/Users/gcrowell/Documents/git/SourceSearch/bootstrap/config
    else
        printf "elasticsearch not installed\n";
        exit 1;
    fi
}
#TODO see https://www.elastic.co/guide/en/elasticsearch/reference/current/important-settings.html
elasticsearch -E cluster.name=visier_search_cluster -E node.name=visier_search_node



