resource "aws_secretsmanager_secret" "pg_url" {
  name = "PG_URL"
}

resource "aws_secretsmanager_secret" "pg_user" {
  name = "PG_USER"
}

resource "aws_secretsmanager_secret" "hcim_rp_endpoint" {
  name = "HCIM_RP_ENDPOINT"
}

resource "aws_secretsmanager_secret" "ssl_pwd" {
  name = "SSL_PWD"
}

resource "aws_secretsmanager_secret" "hcim_ssl_key_pwd" {
  name = "HCIM_SSL_KEY_PWD"
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

resource "aws_secretsmanager_secret_version" "jdbc_setting" {
  secret_id     = aws_secretsmanager_secret.jdbc_setting.id
  secret_string = "changeme"
}

resource "aws_secretsmanager_secret" "deathdate_provider_uri" {
  name = "${var.application}_provider_uri"
}

resource "aws_secretsmanager_secret" "deathdate_siteminder_uri" {
  name = "${var.application}_siteminder_uri"
}

resource "aws_secretsmanager_secret_version" "rds_credentials" {
  secret_id     = aws_secretsmanager_secret.pg_user.id
  secret_string = <<EOF
{
  "username": "deathdate_proxy_user",
  "password": "changeme",
  "engine": "${data.aws_rds_engine_version.postgresql.version}",
  "host": "${module.aurora_postgresql_v2.cluster_endpoint}",
  "port": ${module.aurora_postgresql_v2.cluster_port},
  "dbClusterIdentifier": "${module.aurora_postgresql_v2.cluster_id}"
}
EOF
  lifecycle {
    ignore_changes = [secret_string]
  }
}

resource "aws_secretsmanager_secret_version" "deathdate_keycloak-client-secret" {
  secret_id     = aws_secretsmanager_secret.deathdate_keycloak-client-secret.id
  secret_string = "changeme"
}

resource "aws_secretsmanager_secret_version" "redirect_uri" {
  secret_id     = aws_secretsmanager_secret.redirect_uri.id
  secret_string = "changeme"
}

resource "aws_secretsmanager_secret_version" "deathdate_provider_uri" {
  secret_id     = aws_secretsmanager_secret.deathdate_provider_uri.id
  secret_string = "changeme"
}

resource "aws_secretsmanager_secret_version" "deathdate_siteminder_uri" {
  secret_id     = aws_secretsmanager_secret.deathdate_siteminder_uri.id
  secret_string = "changeme"
}
