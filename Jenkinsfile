node {
  git url: 'https://github.com/jcustenborder/connect-utils.git'
  def mvnHome = tool 'M3'
  sh "${mvnHome}/bin/mvn -B clean package"
  step([$class: 'JUnitResultArchiver', testResults:'**/target/surefire-reports/TEST-*.xml'])
}