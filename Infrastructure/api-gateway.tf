resource "aws_api_gateway_rest_api" "dd-filedrop-api" {
  name = "${var.application}-${var.target_env}-esb-audit-file-drop-presigend-url-api"
  endpoint_configuration {
    types = ["REGIONAL"]
  }
}

resource "aws_api_gateway_resource" "dd-filedrop-gateway" {
  rest_api_id = aws_api_gateway_rest_api.dd-filedrop-api.id
  parent_id   = aws_api_gateway_rest_api.dd-filedrop-api.root_resource_id
  path_part   = "hcim-death-date-esb-audit-file-drop-presigned-url"
}

resource "aws_api_gateway_method" "dd-filedrop-method" {
  rest_api_id   = aws_api_gateway_rest_api.dd-filedrop-api.id
  resource_id   = aws_api_gateway_resource.dd-filedrop-gateway.id
  http_method   = "ANY"
  authorization = "NONE"
  api_key_required = true
}

resource "aws_api_gateway_integration" "gis-integration" {
  rest_api_id = aws_api_gateway_rest_api.dd-filedrop-api.id
  resource_id = aws_api_gateway_resource.dd-filedrop-gateway.id
  http_method = aws_api_gateway_method.dd-filedrop-method.http_method

  type                    = "AWS_PROXY"
  integration_http_method = "POST"
  uri                     = aws_lambda_function.terraform_lambda_func.invoke_arn
}

resource "aws_api_gateway_method_response" "dd-filedrop-response" {
  rest_api_id = aws_api_gateway_rest_api.dd-filedrop-api.id
  resource_id = aws_api_gateway_resource.dd-filedrop-gateway.id
  http_method = aws_api_gateway_method.dd-filedrop-method.http_method
  status_code = "200"
}

resource "aws_api_gateway_integration_response" "dd-filedrop-int-response" {
  rest_api_id = aws_api_gateway_rest_api.dd-filedrop-api.id
  resource_id = aws_api_gateway_resource.dd-filedrop-gateway.id
  http_method = aws_api_gateway_method.dd-filedrop-method.http_method
  status_code = aws_api_gateway_method_response.dd-filedrop-response.status_code
  depends_on = [
    aws_api_gateway_integration.gis-integration
  ]
}

resource "aws_api_gateway_deployment" "dd-filedrop-api-deploy" {
  depends_on  = [aws_api_gateway_method.dd-filedrop-method]
  rest_api_id = aws_api_gateway_rest_api.dd-filedrop-api.id
}

resource "aws_api_gateway_stage" "dd-filedrop-stage" {
  deployment_id = aws_api_gateway_deployment.dd-filedrop-api-deploy.id
  rest_api_id   = aws_api_gateway_rest_api.dd-filedrop-api.id
  stage_name    = "default"
}

resource "aws_api_gateway_usage_plan" "dd-filedrop-usage-plan" {
  name = "dd-filedrop-usage-plan"

  api_stages {
    api_id = aws_api_gateway_rest_api.dd-filedrop-api.id
    stage  = aws_api_gateway_stage.dd-filedrop-stage.stage_name
  }
}

resource "aws_api_gateway_api_key" "dd-filedrop-api-key" {
  name = "hcim-death-date-audit-file-drop-presigned-url-key"
}

resource "aws_api_gateway_usage_plan_key" "dd-filedrop-usage-planl-key" {
  key_id        = aws_api_gateway_api_key.dd-filedrop-api-key.id
  key_type      = "API_KEY"
  usage_plan_id = aws_api_gateway_usage_plan.dd-filedrop-usage-plan.id
}
