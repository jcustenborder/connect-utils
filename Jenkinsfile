#!groovy
node {
    def mvnHome = tool 'M3'
    checkout scm

    if (env.BRANCH_NAME == 'master') {
        stage 'versioning'
        sh "${mvnHome}/bin/mvn -B versions:set -DgenerateBackupPoms=false -DnewVersion=0.1.${env.BUILD_NUMBER}"
        mvn -B versions:set
    }
    stage 'build'

    sh "${mvnHome}/bin/mvn -B clean package"
    junit '**/target/surefire-reports/TEST-*.xml'
}