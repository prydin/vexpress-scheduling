pipeline {
    agent any

    stages {
        stage('Init') {
            steps {
                script {
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
                archiveArtifacts(artifacts: "build/libs/vexpress-scheduling-${env.version}.jar", fingerprint: true, onlyIfSuccessful: true)
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
                parallel(
                        "appServer": {
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
                        },
                        "rabbitServer": {
                            withCredentials([usernamePassword(credentialsId: 'sshCreds', passwordVariable: 'PASSWORD', usernameVariable: 'USER'),
                                             usernamePassword(credentialsId: 'rabbitMqCreds', passwordVariable: 'RABBIT_PASSWORD', usernameVariable: 'RABBIT_USER')]) {
                                script {
                                    def depId = vraDeployFromCatalog(
                                            configFormat: "yaml",
                                            config: readFile('infra/rabbitserver.yaml'))[0].id
                                    vraWaitForAddress(
                                            deploymentId: depId,
                                            resourceName: 'RabbitMQ')[0]
                                    env.rabbitIp = getInternalAddress(depId, "RabbitMQ")
                                    echo "Deployed: ${depId} address: ${env.appIp}"
                                }
                            }
                        },
                )
            }
        }

        stage('Configure') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'sshCreds', passwordVariable: 'PASSWORD', usernameVariable: 'USER'),
                                 usernamePassword(credentialsId: 'rabbitMqCreds', passwordVariable: 'RABBIT_PASSWORD', usernameVariable: 'RABBIT_USER')]) {
                    script {
                        def txt = readFile(file: 'templates/application-properties.tpl')
                        txt = txt.replace('$RABBITMQ_URL', env.rabbitIp).
                                replace('$RABBIT_USER', env.RABBIT_USER).
                                replace('$RABBIT_PASSWORD', env.RABBIT_PASSWORD)
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
                        sshPut remote: remote, from: 'scripts/vexpress-scheduling.service', into: '/tmp'
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

