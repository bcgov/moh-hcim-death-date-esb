variable "target_env" {
  description = "AWS workload account env (e.g. dev, test, prod, sandbox, unclass)"
}

variable "aws_region" {
  description = "The AWS region things are created in"
  default     = "ca-central-1"
}

#variable "project" {
#  description = "The AWS OCIO LicensePlate"
#}

variable "ecs_task_execution_role_name" {
  description = "ECS task execution role name"
  default     = "deathdate_EcsTaskExecutionRole"
}

variable "fargate_cpu" {
  description = "Fargate instance CPU units to provision (1 vCPU = 1024 CPU units)"
  default     = 512
}

variable "fargate_memory" {
  description = "Fargate instance memory to provision (in MiB)"
  default     = 1024
}

variable "deathdate_cluster_name" {
  description = "Name for the FAM database cluster -- must be unique"
  type        = string
  default     = "deathdate-cluster"
}

variable "cluster_name" {
  description = "Name for ECS cluster (with underscore)"
  type        = string
  default     = "deathdate_cluster"
}

variable "ecs_service_name" {
  description = "Name for the ECS service name"
  type        = string
  default     = "deathdate-dev-service"
}

variable "common_tags" {
  description = "Common tags for created resources"
  default = {
    Application = "deathdate"
  }
}

variable "health_check_path" {
  default = "/"
}

variable "alb_name" {
  description = "Name of the internal alb"
  default     = "default"
  type        = string
}

variable "acl_value" {
  default = "private"
}

variable "app_port" {
  description = "Port exposed by the docker image to redirect traffic to"
  default     = 8181
}

variable "app_image" {
  description = "Docker image to run in the ECS cluster. _Note_: there is a blank default value, which will cause service and task resource creation to be supressed unless an image is specified."
  type        = string
  default     = ""
}

variable "app_count" {
  description = "Number of docker containers to run"
  default     = 1
}

variable "application" {
  description = "name of application being executed"
}

variable "alb_origin_id" {
  description = "Domain name of OCIO Perimeter loadbalancer"
}

variable "fam_console_idp_name" {
  description = "Identifies which version of IDIR to use (DEV, TEST, or PROD)"
  type        = string
}

variable "db_instance_identifier" {
  description = "Identifies the cluster ID of aurora_rds_v2"
  default     = "deathdate-cluster"
}

variable "timestamp" {
  type = string
}
