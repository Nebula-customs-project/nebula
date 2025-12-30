# SonarQube Setup Guide

## 1. Start SonarQube Server

```bash
cd docker
docker compose up -d sonarqube sonarqube-db
```

Wait ~2 minutes for startup. Access at: **http://localhost:9000**

---

## 2. Initial Login

| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `admin` |

You'll be prompted to change the password on first login.

---

## 3. Generate Project Token

1. Click **Create Project** → **Manually**
2. Enter:
   - Project display name: `world-view`
   - Project key: `world-view`
3. Click **Set Up**
4. Select **Locally**
5. Click **Generate** token → Copy the token (e.g., `sqp_xxxxxxxxxxxx`)
6. Select **Maven** as build tool

---

## 4. Add SonarQube Plugin to pom.xml

Add to your `pom.xml` under `<build><plugins>`:

```xml
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>4.0.0.4121</version>
</plugin>
```

---

## 5. Run Analysis

From the `services/world-view` directory:

```bash
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=world-view \
  -Dsonar.projectName=world-view \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_TOKEN_HERE
```

Replace `YOUR_TOKEN_HERE` with the token from Step 3.

---

## 6. View Results

Open **http://localhost:9000** → Click on `world-view` project.

---

## Quick Reference

| URL | Purpose |
|-----|---------|
| http://localhost:9000 | SonarQube Dashboard |
| http://localhost:9000/projects | All Projects |
| http://localhost:9000/account/security | Generate Tokens |

## Stop SonarQube

```bash
docker compose stop sonarqube sonarqube-db
```
