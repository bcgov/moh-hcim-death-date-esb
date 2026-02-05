resource "aws_iam_role" "lambda_role" {
  name               = "FileDrop_Lambda_Function_Role"
  assume_role_policy = <<EOF
{
 "Version": "2012-10-17",
 "Statement": [
   {
     "Action": "sts:AssumeRole",
     "Principal": {
       "Service": "lambda.amazonaws.com"
     },
     "Effect": "Allow",
     "Sid": ""
   }
 ]
}
EOF
}

resource "aws_iam_policy" "iam_policy_for_lambda" {

  name   = "aws_iam_policy_for_terraform_aws_lambda_role"
  path   = "/"
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:*",
                "s3-object-lambda:*"
            ],
            "Resource": "*"
        }
    ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "attach_basic_execution_to_iam_role" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}
resource "aws_iam_role_policy_attachment" "attach_iam_policy_to_iam_role" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = aws_iam_policy.iam_policy_for_lambda.arn
}

data "archive_file" "zip_the_python_code" {
  type        = "zip"
  source_file = "hcim-pre-signed-key.py"
  output_path = "${path.root}/hcim-pre-signed-key.zip"
}

resource "aws_lambda_function" "terraform_lambda_func" {
  filename      = "${path.root}/hcim-pre-signed-key.zip"
  function_name = "hcim-death-date-s3-lambda-file-drop"
  role          = aws_iam_role.lambda_role.arn
  handler       = "hcim-pre-signed-key.lambda_handler"
  runtime       = "python3.11"
  environment {
    variables = {
      s3bucket = "${aws_s3_bucket.deathdate-filedrops.id}"
    }
  }
  depends_on = [aws_iam_role_policy_attachment.attach_iam_policy_to_iam_role]
}

resource "aws_lambda_permission" "apigw" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.terraform_lambda_func.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.dd-filedrop-api.execution_arn}/*/*/hcim-death-date-esb-audit-file-drop-presigned-url"
}

resource "aws_cloudwatch_log_group" "hcim_lambda" {
  name = "/aws/lambda/hcim-death-date-s3-lambda-file-drop"
  retention_in_days = 30
}