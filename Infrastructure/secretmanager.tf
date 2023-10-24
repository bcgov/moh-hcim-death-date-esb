resource "aws_secretsmanager_secret" "pg_user" {
  name = "PG_USER"
}

resource "aws_secretsmanager_secret" "hcim_rp_endpoint" {
  name = "HCIM_RP_ENDPOINT"
}

resource "aws_secretsmanager_secret" "hcim_ssl_pwd" {
  name = "SSL_PWD"
}

resource "aws_secretsmanager_secret" "hcim_ssl_key_pwd" {
  name = "HCIM_SSL_KEY_PWD"
}
resource "aws_secretsmanager_secret" "pg_url" {
  name = "PG_URL"
}

resource "aws_secretsmanager_secret" "hcim_ssl_trust_pwd" {
  name = "HCIM_SSL_TRUST_PWD"
}

resource "aws_secretsmanager_secret" "hcim_file_drop_path" {
  name = "HCIM_FILE_DROP_PATH"
}

resource "aws_secretsmanager_secret" "hcim_file_archive_path" {
  name = "HCIM_FILE_ARCHIVE_PATH"
}

resource "aws_secretsmanager_secret" "ftp_host" {
  name = "FTP_HOST"
}

resource "aws_secretsmanager_secret" "ftp_user" {
  name = "FTP_USER"
}

resource "aws_secretsmanager_secret" "ftp_file_path" {
  name = "FTP_FILE_PATH"
}

resource "aws_secretsmanager_secret" "ftp_pwd" {
  name = "FTP_PWD"
}

resource "aws_secretsmanager_secret" "ftp_private_key_name" {
  name = "FTP_PRIVATE_KEY_NAME"
}

resource "aws_secretsmanager_secret" "jms_broker_url" {
  name = "JMS_BROKER_URL"
}

resource "aws_secretsmanager_secret" "api_key" {
  name = "API_KEY_PRESIGNED_S3"
}

resource "aws_secretsmanager_secret" "api_url" {
  name = "API_URL_PRESIGNED_S3"
}

resource "aws_secretsmanager_secret_version" "rds_credentials" {
  secret_id     = aws_secretsmanager_secret.pg_user.id
  secret_string = <<EOF
{
  "username": "deathdate_proxy_user",
  "password": "changeme"
}
EOF
  lifecycle {
    ignore_changes = [secret_string]
  }
}

resource "aws_secretsmanager_secret_version" "hcim_rp_endpoint" {
  secret_id     = aws_secretsmanager_secret.hcim_rp_endpoint.id
  secret_string = "changeme"
}

resource "aws_secretsmanager_secret_version" "hcim_ssl_pwd" {
  secret_id     = aws_secretsmanager_secret.hcim_ssl_pwd.id
  secret_string = "changeme"
}

resource "aws_secretsmanager_secret_version" "pg_url" {
  secret_id     = aws_secretsmanager_secret.pg_url.id
  secret_string = "changeme"
}

resource "aws_secretsmanager_secret_version" "hcim_ssl_key_pwd" {
  secret_id     = aws_secretsmanager_secret.hcim_ssl_key_pwd.id
  secret_string = "changeme"
}

resource "aws_secretsmanager_secret_version" "hcim_ssl_trust_pwd" {
  secret_id     = aws_secretsmanager_secret.hcim_ssl_trust_pwd.id
  secret_string = "changeme"
}

resource "aws_secretsmanager_secret_version" "hcim_file_drop_path" {
  secret_id     = aws_secretsmanager_secret.hcim_file_drop_path.id
  secret_string = "changeme"
}

resource "aws_secretsmanager_secret_version" "hcim_file_archive_path" {
  secret_id     = aws_secretsmanager_secret.hcim_file_archive_path.id
  secret_string = "changeme"
}

resource "aws_secretsmanager_secret_version" "ftp_host" {
  secret_id     = aws_secretsmanager_secret.ftp_host.id
  secret_string = "changeme"
}

resource "aws_secretsmanager_secret_version" "ftp_user" {
  secret_id     = aws_secretsmanager_secret.ftp_user.id
  secret_string = "changeme"
}
resource "aws_secretsmanager_secret_version" "ftp_file_path" {
  secret_id     = aws_secretsmanager_secret.ftp_file_path.id
  secret_string = "changeme"
}
resource "aws_secretsmanager_secret_version" "ftp_pwd" {
  secret_id     = aws_secretsmanager_secret.ftp_pwd.id
  secret_string = "changeme"
}
resource "aws_secretsmanager_secret_version" "ftp_private_key_name" {
  secret_id     = aws_secretsmanager_secret.ftp_private_key_name.id
  secret_string = "changeme"
}
resource "aws_secretsmanager_secret_version" "jms_broker_url" {
  secret_id     = aws_secretsmanager_secret.jms_broker_url.id
  secret_string = aws_mq_broker.deathdate_queue.instances.0.endpoints.0
}
resource "aws_secretsmanager_secret_version" "api_key" {
  secret_id     = aws_secretsmanager_secret.api_key.id
  secret_string = "changeme"
}
resource "aws_secretsmanager_secret_version" "api_url" {
  secret_id     = aws_secretsmanager_secret.api_url.id
  secret_string = "changeme"
}
