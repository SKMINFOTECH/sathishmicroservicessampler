provider "aws" {
  region = "us-east-1"
}
variable "bucketname" {
  type    = string
  default = "satss3webbucket"
}
variable "bucketnamelist" {
  type    = list
  default = ["1", "2"]
}
variable "bucketnamemaps" {
  type    = map
  default = {
    key = "Value1"
    key = "value2"
  }
}

resource "aws_vpc" "s3vpc" {
  cidr_block = "10.0.0.0/16"
}
resource "aws_s3_bucket" "mywebsites" {
  bucket = "satswebbucket"
  tags   = {
    Name = "${var.bucketname}"
  }
}

resource "aws_s3_bucket_public_access_block" "mywebsitespublicblock" {
  bucket                  = aws_s3_bucket.mywebsites.id
  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}
resource "aws_s3_bucket_website_configuration" "s3bucketwebsite_config" {
  bucket = aws_s3_bucket.mywebsites.id

  index_document {
    suffix = "index.html"
  }

  error_document {
    key = "error.html"
  }

  routing_rule {
    condition {
      key_prefix_equals = "docs/"
    }
    redirect {
      replace_key_prefix_with = "documents/"
    }
  }
}



output "s3objectdata" {
  value = aws_s3_bucket.mywebsites.arn
}
