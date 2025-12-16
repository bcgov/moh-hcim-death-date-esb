resource "aws_mq_broker" "deathdate_queue" {
  broker_name                = "DeathDate-Broker"
  engine_type                = "ActiveMQ"
  engine_version             = "5.18"
  host_instance_type         = "mq.t3.micro"
  auto_minor_version_upgrade = true
  deployment_mode            = "SINGLE_INSTANCE"
  security_groups            = [data.aws_security_group.app.id, data.aws_security_group.data.id]
  #fix subnet id
  #subnet_ids = [data.aws_subnet.app["subnet-0896ff158c3ecdc53"].id]
  subnet_ids = [values(data.aws_subnet.app)[0].id]
  logs {
    audit   = true
    general = true
  }
  user {
    username = "Dev"
    password = "changemenow123"
  }
}
