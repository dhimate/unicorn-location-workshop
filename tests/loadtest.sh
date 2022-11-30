docker run --rm -it -v ${PWD}:/scripts \
	  artilleryio/artillery:latest \
	    run -t ${API_GW_URL} /scripts/loadtest.yaml
