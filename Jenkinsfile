#!groovy
node {
  git credentialsId: '1a3dea93-e1d6-4568-8be6-5af1c51896b1', url: 'git@github.com:jcustenborder/connect-utils.git'
  checkout scm
  def mvnHome = tool 'M3'
  sh "${mvnHome}/bin/mvn -B clean package"
  step([$class: 'JUnitResultArchiver', testResults:'**/target/surefire-reports/TEST-*.xml'])
}