curl -XPUT \
  --url ${API_GW_URL}$1 \
  --header 'content-type: application/json' \
  --data '{
  "unicornName": "Vaidehi 123",
  "longitude": "13.404954",
  "latitude": "52.520008"
}'
