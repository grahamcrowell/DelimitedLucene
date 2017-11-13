http://localhost:9200/_stats/customer?pretty&fielddata=true
http://localhost:9200/_cat/indices?v


http://localhost:9200/_cat/indices?format=json&pretty=true

curl -XPUT 'localhost:9200/customer?pretty&pretty'
curl -XGET 'localhost:9200/_cat/indices?v&pretty'

http://localhost:9600/_node/pipeline?pretty

http://localhost:9600/_node/stats/?pretty