resource "random_pet" "deathdate_subnet_group_name" {
  prefix = "deathdate-subnet-group"
  length = 2
}


resource "random_password" "deathdate_master_password" {
  length           = 16
  special          = true
  override_special = "!#$%&*()-_=+[]{}<>:?"
}

variable "deathdate_master_username" {
  description = "The username for the DB master user"
  type        = string
  default     = "postgres"
  sensitive   = true
}

variable "deathdate_database_name" {
  description = "The name of the database"
  type        = string
  default     = "deathdate"
}

resource "aws_db_subnet_group" "deathdate_subnet_group" {
  description = "For Aurora cluster ${var.deathdate_cluster_name}"
  name        = "${var.deathdate_cluster_name}-subnet-group"
  subnet_ids  = data.aws_subnets.app.ids
  tags = {
    managed-by = "terraform"
  }

  tags_all = {
    managed-by = "terraform"
  }
}

module "postgres_rds" {
  source = "terraform-aws-modules/rds/aws"

  identifier = "${var.application}-${var.target_env}-audit"

  engine            = "postgresql"
  engine_version    = "13.9"
  instance_class    = "db.t3.micro"
  allocated_storage = 5

  db_name  = "demodb"
  username = "${var.deathdate_master_username}"
  password = "${random_password.deathdate_master_password.result}"
  port     = "5432"

  vpc_security_group_ids = [data.aws_security_group.data.id]

  maintenance_window = "Mon:00:00-Mon:03:00"
  backup_window      = "03:00-06:00"

  # Enhanced Monitoring - see example for details on how to create the role
  # by yourself, in case you don't want to create it automatically
  monitoring_interval    = "30"
  monitoring_role_name   = "MyRDSMonitoringRole"
  create_monitoring_role = true

  tags = {
    CreatedBy = "terraform"
  }

  # DB subnet group
  create_db_subnet_group = true
  subnet_ids             = [data.aws_subnets.data.ids]


  # Database Deletion Protection
  #deletion_protection = true
}

resource "aws_db_parameter_group" "deathdate_postgresql13" {
  name        = "${var.deathdate_cluster_name}-parameter-group"
  family      = "postgresq13"
  description = "${var.deathdate_cluster_name}-parameter-group"
  tags = {
    managed-by = "terraform"
  }
}

resource "aws_rds_cluster_parameter_group" "deathdate_postgresql13" {
  name        = "${var.deathdate_cluster_name}-cluster-parameter-group"
  family      = "postgres13"
  description = "${var.deathdate_cluster_name}-cluster-parameter-group"
  tags = {
    managed-by = "terraform"
  }
}

resource "random_pet" "master_creds_secret_name" {
  prefix = "deathdate-master-creds"
  length = 2
}

resource "aws_secretsmanager_secret" "deathdate_mastercreds_secret" {
  name = random_pet.master_creds_secret_name.id

  tags = {
    managed-by = "terraform"
  }
}

resource "aws_secretsmanager_secret_version" "deathdate_mastercreds_secret_version" {
  secret_id     = aws_secretsmanager_secret.deathdate_mastercreds_secret.id
  secret_string = <<EOF
   {
    "username": "${var.deathdate_master_username}",
    "password": "${random_password.deathdate_master_password.result}"
   }
  EOF
  lifecycle {
  ignore_changes = [ secret_string  ]
  }
}

resource "random_password" "deathdate_api_password" {
  length           = 16
  special          = true
  override_special = "!#$%&*()-_=+[]{}<>:?"
}

variable "deathdate_api_username" {
  description = "The username for the DB api user"
  type        = string
  default     = "fam_proxy_api"
  sensitive   = true
}


resource "random_pet" "api_creds_secret_name" {
  prefix = "deathdate-api-creds"
  length = 2
}

resource "aws_secretsmanager_secret" "deathdate_apicreds_secret" {
  name = random_pet.api_creds_secret_name.id

  tags = {
    managed-by = "terraform"
  }
}

resource "aws_secretsmanager_secret_version" "deathdate_apicreds_secret_version" {
  secret_id     = aws_secretsmanager_secret.deathdate_apicreds_secret.id
  secret_string = <<EOF
   {
    "username": "${var.deathdate_api_username}",
    "password": "${random_password.deathdate_api_password.result}"
   }
  EOF
  lifecycle {
  ignore_changes = [ secret_string  ]
  }
}