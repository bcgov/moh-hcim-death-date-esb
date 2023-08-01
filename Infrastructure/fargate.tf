resource "aws_cloudwatch_log_group" "ecs_monitoring" {
  name = "ecs-${var.application}-monitoring"
  retention_in_days = "90"
}

resource "aws_ecs_cluster" "deathdate_cluster" {
  name = "deathdate_cluster"
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
      #change to variable to env. for GH Actions
      image       = "${data.aws_caller_identity.current.account_id}.dkr.ecr.ca-central-1.amazonaws.com/deathdate:latest"
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
        {"name": "PG_USER", 
         "valueFrom": "${aws_secretsmanager_secret_version.rds_credentials.arn}:username::"},
        {"name": "PG_PASSWORD", 
         "valueFrom": "${aws_secretsmanager_secret_version.rds_credentials.arn}:password::"},
        {"name": "JDBC_SETTING", 
         "valueFrom": "${aws_secretsmanager_secret_version.jdbc_setting.arn}"},
         {"name": "deathdate_keycloak_client_secret",
         "valueFrom": "${aws_secretsmanager_secret_version.deathdate_keycloak-client-secret.arn}"},
         {"name": "REDIRECT_URI",
         "valueFrom": "${aws_secretsmanager_secret_version.redirect_uri.arn}"},
         {"name": "SITEMINDER_URI",
         "valueFrom": "${aws_secretsmanager_secret_version.deathdate_siteminder_uri.arn}"},
         {"name": "PROVIDER_URI",
         "valueFrom": "${aws_secretsmanager_secret_version.deathdate_provider_uri.arn}"}
      ]
      environment = [
        {"name": "XMX_DEV",
         "value": "\\-Xmx1024m"},
        {"name": "XMS_DEV",
         "value": "\\-Xms512m"}
      ]
      #change awslog group
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