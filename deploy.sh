sam build 
sam deploy
export API_GW_URL=$(aws cloudformation describe-stacks --stack-name unicorn-location-api | jq -r '.Stacks[0].Outputs[] | select(.OutputKey == "UnicornLocationApi").OutputValue')


