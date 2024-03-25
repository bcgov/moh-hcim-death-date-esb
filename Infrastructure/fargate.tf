resource "aws_ecs_cluster" "deathdate_cluster" {
  name = "${var.application}_cluster"
}

resource "aws_ecs_cluster_capacity_providers" "deathdate_cluster" {
  cluster_name       = aws_ecs_cluster.deathdate_cluster.name
  capacity_providers = ["FARGATE"]

  default_capacity_provider_strategy {
    capacity_provider = "FARGATE"
    weight            = 100

  }
}

resource "aws_ecs_task_definition" "deathdate_td" {
  family                   = "${var.application}-${var.target_env}-task"
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  task_role_arn            = aws_iam_role.ecs_task_execution_role.arn
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.fargate_cpu
  memory                   = var.fargate_memory
  tags                     = local.common_tags
  container_definitions = jsonencode([
    {
      essential   = true
      name        = "${var.application}-${var.target_env}-definition"
      image       = "${data.aws_caller_identity.current.account_id}.dkr.ecr.ca-central-1.amazonaws.com/hcim-death-date-esb:latest"
      cpu         = var.fargate_cpu
      memory      = var.fargate_memory
      networkMode = "awsvpc"
      portMappings = [
        {
          protocol      = "tcp"
          containerPort = var.app_port
          hostPort      = var.app_port
        }
      ]
      secrets = [
        { name = "PG_URL",
        valueFrom = "${aws_secretsmanager_secret_version.pg_url.arn}" },
        { name = "PG_USER",
        valueFrom = "${aws_secretsmanager_secret_version.rds_credentials.arn}:username::" },
        { name = "PG_PASSWORD",
        valueFrom = "${aws_secretsmanager_secret_version.rds_credentials.arn}:password::" },
        { name = "HCIM_REVISED_PERSON_ENDPOINT",
        valueFrom = "${aws_secretsmanager_secret_version.hcim_rp_endpoint.arn}" },
        { name = "HCIM_SSL_PWD",
        valueFrom = "${aws_secretsmanager_secret_version.hcim_ssl_pwd.arn}" },
        { name = "HCIM_SSL_KEY_PWD",
        valueFrom = "${aws_secretsmanager_secret_version.hcim_ssl_key_pwd.arn}" },
        { name = "HCIM_SSL_TRUST_PWD",
        valueFrom = "${aws_secretsmanager_secret_version.hcim_ssl_trust_pwd.arn}" },
        { name = "HCIM_FILE_DROP_PATH",
        valueFrom = "${aws_secretsmanager_secret_version.hcim_file_drop_path.arn}" },
        { name = "HCIM_FILE_ARCHIVE_PATH",
        valueFrom = "${aws_secretsmanager_secret_version.hcim_file_archive_path.arn}" },
        { name = "FTP_HOST",
        valueFrom = "${aws_secretsmanager_secret_version.ftp_host.arn}" },
        { name = "FTP_USER",
        valueFrom = "${aws_secretsmanager_secret_version.ftp_user.arn}" },
        { name = "FTP_FILE_PATH",
        valueFrom = "${aws_secretsmanager_secret_version.ftp_file_path.arn}" },
        { name = "FTP_PRIVATE_KEY_PASSPHRASE",
        valueFrom = "${aws_secretsmanager_secret_version.ftp_private_key_passphrase.arn}" },
        { name = "FTP_PRIVATE_KEY_NAME",
        valueFrom = "${aws_secretsmanager_secret_version.ftp_private_key_name.arn}" },
        { name = "JMS_BROKER_URL",
        valueFrom = "${aws_secretsmanager_secret_version.jms_broker_url.arn}" },
        { name = "API_KEY_PRESIGNED_S3",
        valueFrom = "${aws_secretsmanager_secret_version.api_key.arn}" },
        { name = "API_URL_PRESIGNED_S3",
        valueFrom = "${aws_secretsmanager_secret_version.api_url.arn}" },
        { name = "JMS_BROKER_USERNAME",
        valueFrom = "${aws_secretsmanager_secret_version.jms_user.arn}" },
        { name = "JMS_BROKER_PASSWORD",
        valueFrom = "${aws_secretsmanager_secret_version.jms_pass.arn}" }
      ]
      environment = [
        { name = "APP_LOG_LEVEL",
        value = "ERROR" },
        { name = "CAMEL_LOG_LEVEL",
        value = "INFO" },
        { name = "SCHEDULER_CRON",
        value = "0 0 6,12 * * *" },
        { name = "JVM_ARGS",
        value = "-Xms512m -Xmx756m" }
      ]

      logConfiguration = {
        "logDriver" : "awslogs",
        "options" : {
          "awslogs-create-group" = "true"
          "awslogs-group" : "/ecs/${var.application}",
          "awslogs-region" : "${var.aws_region}",
          "awslogs-stream-prefix" : "streaming"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "main" {
  name                  = "${var.application}-${var.target_env}-service"
  cluster               = aws_ecs_cluster.deathdate_cluster.arn
  task_definition       = aws_ecs_task_definition.deathdate_td.arn
  desired_count         = 1
  wait_for_steady_state = false
  force_new_deployment  = true

  triggers = {
    redeployment = var.timestamp
  }

  network_configuration {
    security_groups  = [data.aws_security_group.app.id]
    subnets          = data.aws_subnets.app.ids
    assign_public_ip = false
  }

  depends_on = [aws_iam_role_policy_attachment.ecs_task_execution_role]

  lifecycle {
    ignore_changes = [capacity_provider_strategy]
  }

}
