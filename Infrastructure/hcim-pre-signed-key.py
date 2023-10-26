import boto3, json, os

"""
Params:
    The parameters of the specified client method.
    The bucket is always hcim-death-date-esb-audit-file-drop-jy4vrd-dev.

ExpiresIn:
    The number of seconds the presigned URL is valid for.
    Hard coded for 60 seconds.
"""

def lambda_handler(event, context):
    s3_client = boto3.client("s3")

    url = s3_client.generate_presigned_url(
        ClientMethod = "put_object",
        Params = {
            "Bucket": os.environ["s3bucket"],
            "Key": event["queryStringParameters"]["key"]
        },
        ExpiresIn = 60
    )

    return {
        "body": json.dumps(url)
    }