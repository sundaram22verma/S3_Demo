# S3Demo - Spring Boot AWS S3 Integration

This project demonstrates how to integrate **Spring Boot** with **Amazon S3** to perform file uploads and generate **presigned URLs** for secure downloads.

---

## Features

- Upload files to S3 using **MultipartFile**.
- Generate **time-limited presigned URLs** for downloading files.
- REST APIs for **file upload** and **presigned URL retrieval**.
- Configurable **AWS credentials, bucket name, and region** via `application.properties`.
- Secure access without exposing AWS secret keys in the frontend.

---

## Prerequisites

- Java 17+
- Maven
- AWS Account with:
  - S3 bucket
  - IAM user with **S3 FullAccess** or a custom S3 policy
- AWS CLI (optional, for profile-based credentials)
- Postman (for testing APIs)

---

## Setup

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/S3Demo.git
cd S3Demo
````

### 2. Configure AWS Credentials

**Option 1: Using AWS CLI profile (Recommended)**

```bash
aws configure
```

* Enter Access Key ID and Secret Access Key of your IAM user.
* Enter default region (e.g., `ap-south-1`).

**Option 2: Using `application.properties`**

```properties
aws.region= <Bucket_Region>  # e.g., ap-south-1
aws.s3.bucket= <Bucket_Name>  # e.g., my-s3-bucket
```
aws.profile=default
# aws.accessKeyId=YOUR_ACCESS_KEY
# aws.secretAccessKey=YOUR_SECRET_KEY
```

> ⚠️ Do not hardcode credentials in production.
> In code, decode the bucket name before using:

```java
String bucketName = new String(Base64.getDecoder().decode(encodedBucketName));
```

---

### 3. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

Application will start at `http://localhost:8080`.

---

## API Endpoints

### 1. Upload File

* **URL:** `POST /s3/upload`
* **Body:** form-data

  | Key     | Type | Description     |
    | ------- | ---- | --------------- |
  | keyName | Text | File name in S3 |
  | file    | File | File to upload  |

**Example using Postman:**

```
POST http://localhost:8080/s3/upload
```

**Response:**

```
✅ File uploaded: enjoy.jpg
```

---

### 2. Get Presigned URL

* **URL:** `GET /s3/download-url`
* **Query Params:**

  | Key     | Description                            |
    | ------- | -------------------------------------- |
  | keyName | Name of the file in S3                 |
  | minutes | Expiration time in minutes (default 5) |

**Example using Postman:**

```
GET http://localhost:8080/s3/download-url?keyName=enjoy.jpg&minutes=5
```

**Response:**

```
https://s3.<region>.amazonaws.com/<ENCRYPTED_BUCKET_NAME>/enjoy.jpg?X-Amz-Algorithm=...
```

> The `<ENCRYPTED_BUCKET_NAME>` is a placeholder. The actual bucket name is stored **encoded in `application.properties`**.

---

## Notes

* Presigned URLs are **temporary and expire** after the given duration.
* Always prefer **AWS CLI profile** for credentials over hardcoding.
* For shorter URLs, you can use **CloudFront or API Gateway redirect**.

---

## Project Structure

```
S3Demo/
├── src/main/java/com/demo/S3Demo/
│   ├── controller/S3Controller.java
│   ├── service/S3Service.java
│   └── S3DemoApplication.java
├── src/main/resources/application.properties
├── pom.xml
└── README.md
```