pipeline {
    agent any

    stages {
        stage('Init') {
            steps {
                script {
                    properties([parameters([string(defaultValue: '',
                            description: 'The zipcode service URL', name: 'ZIPCODE_URL', trim: false)])])
                    def r = /version\s*=\s*["'](.+)["']/
                    def gradle = readFile(file: 'build.gradle')
                    env.version = (gradle =~ /version\s*=\s*["'](.+)["']/)[0][1]
                    echo "Inferred version: ${env.version}"
                }
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean assemble'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
                junit 'build/test-results/test/*.xml'
            }
        }

        stage('Publish') {
            steps {
                archiveArtifacts(artifacts: "build/libs/vexpress-pricing-${env.version}.jar", fingerprint: true, onlyIfSuccessful: true)
            }
        }

        stage("InitDeployment") {
            steps {
                withCredentials([usernamePassword(credentialsId: 'apiToken', passwordVariable: 'PASSWORD', usernameVariable: 'USER')]) {
                    script {
                        env.apiUser = USER
                        env.apiToken = PASSWORD
                    }
                }
            }
        }

        stage('DeployVMs') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'sshCreds', passwordVariable: 'PASSWORD', usernameVariable: 'USER')]) {
                    script {
                        def depId = vraDeployFromCatalog(
                                configFormat: "yaml",
                                config: readFile('infra/appserver.yaml'))[0].id
                        vraWaitForAddress(
                                deploymentId: depId,
                                resourceName: 'JavaServer')[0]
                        env.appIp = getInternalAddress(depId, "JavaServer")
                        echo "Deployed: ${depId} address: ${env.appIp}"
                    }
                }
            }
        }

        stage('Configure') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'sshCreds', passwordVariable: 'PASSWORD', usernameVariable: 'USER')]) {
                    script {
                        def zipUrl = params.ZIPCODE_URL ? params.ZIPCODE_URL : env.DEFAULT_ZIPCODE_URL
                        echo "Zipcode service URL: ${zipUrl}"
                        def txt = readFile(file: 'templates/application-properties.tpl')
                        txt = txt.replace('$ZIPCODE_URL', zipUrl)
                        writeFile(file: "application.properties", text: txt)

                        def remote = [:]
                        remote.name = 'appServer'
                        remote.host = env.appIp
                        remote.user = USER
                        remote.password = PASSWORD
                        remote.allowAnyHosts = true

                        // The first first attempt may fail if cloud-init hasn't created user account yet
                        retry(20) {
                            sleep time: 10, unit: 'SECONDS'
                            sshPut remote: remote, from: 'application.properties', into: '/tmp'
                        }
                        sshPut remote: remote, from: 'scripts/vexpress-pricing.service', into: '/tmp'
                        sshPut remote: remote, from: 'scripts/configureAppserver.sh', into: '/tmp'
                        sshCommand remote: remote, command: 'chmod +x /tmp/configureAppserver.sh'
                        sshCommand remote: remote, sudo: true, command: "/tmp/configureAppserver.sh ${USER} ${env.apiUser} ${env.apiToken} ${env.BUILD_URL} ${env.version}"
                    }
                }
            }
        }
    }
}

def getInternalAddress(id, resourceName) {
    def dep = vraGetDeployment(
            deploymentId: id,
            expandResources: true
    )
    return dep.resources.find({ it.name == resourceName }).properties.networks[0].address
}

