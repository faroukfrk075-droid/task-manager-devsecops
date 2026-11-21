# Task Manager API — DevSecOps CI/CD Pipeline

A lightweight **Spring Boot (Java 21)** REST API built specifically as the target application for an automated **Jenkins DevSecOps pipeline**. Every `git push` automatically triggers an end-to-end build, static/dynamic security audit, containerization, and Kubernetes deployment loop.

---

## Purpose of This Repository

Rather than a standalone production product, this application provides a clean, testable Java backend designed to demonstrate and continuously execute a **100% automated DevSecOps lifecycle** via Jenkins:
- **Continuous Integration & Testing:** Automated compilation and JUnit test execution with Maven.
- **Security at Every Stage:** Static analysis (**SonarQube**), dependency CVE scanning (**OWASP Dependency-Check**), container image auditing (**Trivy**), and dynamic runtime testing (**OWASP ZAP**).
- **Continuous Deployment:** Immutable Docker image builds pushed to **Docker Hub** and rolled out automatically onto **Kubernetes (Minikube)** on every push.

---

## Architecture & Pipeline Flow

```text
Git Push ──► Jenkins ──► Maven Build & JUnit ──► SonarQube (SAST) ──► OWASP Dependency-Check
                                                                                │
OWASP ZAP (DAST) ◄── Minikube (Kubernetes) ◄── Docker Hub ◄── Trivy Scan ◄── Docker Build
