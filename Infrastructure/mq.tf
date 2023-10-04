resource "aws_mq_broker" "deathdate_queue" {
  broker_name = "DeathDate-Broker"
  engine_type        = "ActiveMQ"
  engine_version     = "5.17.3"
  host_instance_type = "mq.t3.micro"
  auto_minor_version_upgrade = true
  deployment_mode = "SINGLE_INSTANCE"
  logs {
    audit = true
    general = true
  }
  security_groups    = [data.aws_security_group.app]
  user {
    username = "Dev"
    password = "changeme"
  }
}