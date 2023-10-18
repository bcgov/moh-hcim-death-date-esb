resource "aws_ecs_cluster" "deathdate_cluster" {
  name = "{var.application}_cluster"
}

resource "aws_ecs_cluster_capacity_providers" "deathdate_cluster" {
  cluster_name               = aws_ecs_cluster.deathdate_cluster.name
  capacity_providers = ["FARGATE"]

  default_capacity_provider_strategy {
    capacity_provider = "FARGATE"
    weight            = 100

  }
}

resource "aws_ecs_task_definition" "deathdate_td" {
  family                   = "deathdate-${var.target_env}-task"
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
      name        = "deathdate-${var.target_env}-definition"
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
        {"name": "PG_URL", 
         "valueFrom": "${aws_secretsmanager_secret_version.rds_credentials.arn}:username::"},
        {"name": "PG_USER", 
         "valueFrom": "${aws_secretsmanager_secret_version.rds_credentials.arn}:username::"},
        {"name": "PG_PASSWORD", 
         "valueFrom": "${aws_secretsmanager_secret_version.rds_credentials.arn}:password::"},
         {"name": "HCIM_REVISED_PERSON_ENDPOINT",
         "valueFrom": "${aws_secretsmanager_secret_version.deathdate_keycloak-client-secret.arn}"},
         {"name": "HCIM_SSL_PWD",
         "valueFrom": "${aws_secretsmanager_secret_version.redirect_uri.arn}"},
         {"name": "HCIM_SSL_KEY_PWD",
         "valueFrom": "${aws_secretsmanager_secret_version.deathdate_siteminder_uri.arn}"},
         {"name": "HCIM_SSL_TRUST_PWD",
         "valueFrom": "${aws_secretsmanager_secret_version.deathdate_provider_uri.arn}"},
         {"name": "HCIM_FILE_DROP_PATH",
         "valueFrom": "${aws_secretsmanager_secret_version.deathdate_provider_uri.arn}"},
         {"name": "HCIM_FILE_ARCHIVE_PATH",
         "valueFrom": "${aws_secretsmanager_secret_version.deathdate_provider_uri.arn}"},
         {"name": "FTP_HOST",
         "valueFrom": "${aws_secretsmanager_secret_version.deathdate_provider_uri.arn}"},
         {"name": "FTP_USER",
         "valueFrom": "${aws_secretsmanager_secret_version.deathdate_provider_uri.arn}"},
         {"name": "FTP_FILE_PATH",
         "valueFrom": "${aws_secretsmanager_secret_version.deathdate_provider_uri.arn}"},
         {"name": "FTP_PWD",
         "valueFrom": "${aws_secretsmanager_secret_version.deathdate_provider_uri.arn}"},
         {"name": "FTP_PRIVATE_KEY_NAME",
         "valueFrom": "${aws_secretsmanager_secret_version.deathdate_provider_uri.arn}"},
         {"name": "JMS_BROKER_URL",
         "valueFrom": "${aws_secretsmanager_secret_version.deathdate_provider_uri.arn}"}
      ]
      environment = [
        {"name": "DEATH_DATE_ENV",
         "value": "Local"},
        {"name": "APP_LOG_LEVEL",
         "value": "ERROR"},
         {"name": "CAMEL_LOG_LEVEL",
         "value": "INFO"},
         {"name": "SCHEDULER_CRON",
         "value": "INFO"},
         {"name": "JVM_ARGS",
         "value": "INFO"}
      ]
      
      logConfiguration = {
      "logDriver": "awslogs",
      "options": {
        "awslogs-group": "${aws_cloudwatch_log_group.ecs_monitoring.name}",
        "awslogs-region": "ca-central-1",
        "awslogs-stream-prefix": "streaming"
        }
      }
    }
  ])
}

resource "aws_ecs_service" "main" {
  name                              = "deathdate-${var.target_env}-service"
  cluster                           = aws_ecs_cluster.deathdate_cluster.arn
  task_definition                   = aws_ecs_task_definition.deathdate_td.arn
  desired_count                     = 2
  #Health Check need to go up?
  health_check_grace_period_seconds = 60
  wait_for_steady_state             = false

  network_configuration {
    security_groups  = [data.aws_security_group.app.id]
    subnets          = data.aws_subnets.app.ids
    assign_public_ip = false
  }

  load_balancer {
    target_group_arn = aws_alb_target_group.app.id
    container_name   = "${var.application}-${var.target_env}-definition"
    container_port   = var.app_port
  }

  depends_on = [data.aws_alb_listener.front_end, aws_iam_role_policy_attachment.ecs_task_execution_role]
  
   lifecycle {
  ignore_changes = [ capacity_provider_strategy ]
  }

}
