# [Lucene](https://lucene.apache.org/) Indexer for Delimited Files

([Lucene 7.0.1 docs](https://lucene.apache.org/core/7_0_1/index.html))

- index delimited files without predefining schema
    - schema is inferred by splitting header
- full text search across multiple folders, files, and columns
- each column in source data is a [Field](https://lucene.apache.org/core/7_0_1/core/org/apache/lucene/document/Field.html) in Lucene
- each line in source data is a [Document](https://lucene.apache.org/core/7_0_1/core/org/apache/lucene/document/Document.html) in Lucene
- each header in source data is a [Index](https://lucene.apache.org/core/7_0_1/core/org/apache/lucene/index/IndexWriter.html)
- each tenant's Lucene index is persisted in a separate [FSDirectory](https://lucene.apache.org/core/7_0_1/core/org/apache/lucene/store/FSDirectory.html)

## High level logic diagram

<div class="mxgraph" style="max-width:100%;border:1px solid transparent;" data-mxgraph="{&quot;highlight&quot;:&quot;#0000ff&quot;,&quot;nav&quot;:true,&quot;resize&quot;:true,&quot;toolbar&quot;:&quot;zoom layers lightbox&quot;,&quot;edit&quot;:&quot;_blank&quot;,&quot;xml&quot;:&quot;&lt;mxfile userAgent=\&quot;Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36\&quot; version=\&quot;8.6.7\&quot; editor=\&quot;www.draw.io\&quot; type=\&quot;google\&quot;&gt;&lt;diagram id=\&quot;52a04d89-c75d-2922-d76d-85b35f80e030\&quot; name=\&quot;Page-1\&quot;&gt;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&lt;/diagram&gt;&lt;/mxfile&gt;&quot;}"></div>
<script type="text/javascript" src="https://www.draw.io/js/viewer.min.js"></script>

## Search results include

- name of matched file
- name (date stamp) of parent folder
- matched column name
- matched line number
- names and values of other columns on matched line

## Overview of Per Tenant Indexing

Each data is organized physically (ie. in `/esldata/`) hierarchially as follows

- Tenant has 1 or more:
    - Dated folder has 1 or more:
        - Delimited data file has 1:
            - Header, delimiter

