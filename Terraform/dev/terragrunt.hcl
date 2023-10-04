include {
  path = find_in_parent_folders()
}
 
inputs = {
environment = "dev"
}

locals {
  project = get_env("LICENSE_PLATE")
#   commontags = [
#     environment = "dev",
#     application = "fmdb"
#   ]
}

generate "dev_tfvars" {
  path              = "dev.auto.tfvars"
  if_exists         = "overwrite"
  disable_signature = true
  contents          = <<-EOF
  fargate_cpu = 512
  fargate_memory = 1024
  app_port = 8181
  fam_console_idp_name = "DEV-IDIR"
  alb_origin_id = "deathdate.jy4drv-dev.nimbus.cloud.gov.bc.ca"
  EOF
}
