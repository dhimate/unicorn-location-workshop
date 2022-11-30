# unicorn-location-workshop

This project contains source code and supporting files for a serverless application that you can deploy with the SAM CLI. It includes the following files and folders.

- UnicornLocationFunction/src/main - Code for the application's Lambda function.
- UnicornLocationLambdaLayer - Lambda layer for java dependencies 
- template.yaml - A template that defines the application's AWS resources.

## Architecture

```mermaid

graph LR
    API["Unicorn Location API (API Gateway)"] --> Post[λ PostLocation] --> DynamoDB[Unicorn Location DynamoDB Table]
    API["Unicorn Location API (API Gateway)"] --> Put[λ PutLocation] --> DynamoDB[Unicorn Location DynamoDB Table]
    API["Unicorn Location API (API Gateway)"] --> Get[λ GetLocation] --> DynamoDB[Unicorn Location DynamoDB Table]
    API["Unicorn Location API (API Gateway)"] --> Delete[ λ DeleteLocation] --> DynamoDB[Unicorn Location DynamoDB Table]


```

## Deploy the application

To build and deploy your application for the first time, run the following in your shell:

```bash
mvn install -f UnicornLocationLambdaLayer/pom.xml 
sam build
sam deploy --guided
```

The `sam build` command will build the source of your application. The `sam deploy` will package and deploy your application to AWS, with a series of prompts:

* **Stack Name**: The name of the stack to deploy to CloudFormation. This should be unique to your account and region, and a good starting point would be something matching your project name.
* **AWS Region**: The AWS region you want to deploy your app to.
* **Confirm changes before deploy**: If set to yes, any change sets will be shown to you before execution for manual review. If set to no, the AWS SAM CLI will automatically deploy application changes.
* **Allow SAM CLI IAM role creation**: Many AWS SAM templates, including this example, create AWS IAM roles required for the AWS Lambda function(s) included to access AWS services. By default, these are scoped down to minimum required permissions. To deploy an AWS CloudFormation stack which creates or modifies IAM roles, the `CAPABILITY_IAM` value for `capabilities` must be provided. If permission isn't provided through this prompt, to deploy this example you must explicitly pass `--capabilities CAPABILITY_IAM` to the `sam deploy` command.
* **Save arguments to samconfig.toml**: If set to yes, your choices will be saved to a configuration file inside the project, so that in the future you can just re-run `sam deploy` without parameters to deploy changes to your application.

You can find your API Gateway Endpoint URL in the output values displayed after deployment.


## Cleanup

To delete the sample application that you created, use the AWS CLI. Assuming you used your project name for the stack name, you can run the following:

```bash
aws cloudformation delete-stack --stack-name unicorn-location-workshop
```
