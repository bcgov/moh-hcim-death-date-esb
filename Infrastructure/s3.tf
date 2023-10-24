#S3 Filedrop Bucket

resource "aws_s3_bucket" "deathdate-filedrops" {
  bucket = "${var.application}-audit-file-drop-${var.project}-${var.target_env}"
}