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
