# Feed Ask AI - Intelligent PDF Q&A System

A production-grade Spring Boot application that enables intelligent document Q&A through semantic chunking, vector embeddings, and LLM-powered retrieval augmented generation (RAG). Upload PDF documents, and ask natural language questions to get context-aware answers powered by Ollama.

## ✨ Features

- **📄 Intelligent PDF Processing**: Advanced semantic text chunking that respects document structure
- **🧠 Semantic Embeddings**: Uses Ollama's `nomic-embed-text` model for high-quality vector embeddings
- **⚡ Fast Vector Search**: Efficient similarity-based retrieval using cosine similarity
- **🤖 AI-Powered Answers**: LLM-based answer generation using `tinyllama` model
- **📦 Persistent Storage**: MySQL database with optimized schema for embeddings
- **📊 Document Tracking**: Full lifecycle tracking (PENDING → PROCESSING → COMPLETED)
- **🔒 Content Deduplication**: SHA-256 file hashing prevents duplicate uploads
- **📖 OpenAPI Documentation**: Full Swagger UI integration
- **🚀 Production-Ready**: Comprehensive error handling, logging, and status tracking
- **🐳 Docker Integration**: Automated containerized setup with health checks

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Client Application                   │
└────────────────┬───────────────────────┬────────────────────┘
                 │                       │
          ┌──────▼──────┐         ┌──────▼──────┐
          │ Upload PDF  │         │ Ask Question│
          │   Endpoint  │         │   Endpoint  │
          └──────┬──────┘         └──────┬──────┘
                 │                       │
    ┌────────────▼───────────────────────▼─────────────┐
    │    SPRING BOOT APPLICATION (Java 21)             │
    │  ┌──────────────────────────────────────────┐    │
    │  │ FeedService (Document Upload)            │    │
    │  │ - Semantic Text Chunking                 │    │
    │  │ - Embedding Generation                   │    │
    │  │ - Deduplication                          │    │
    │  └──────────────────────────────────────────┘    │
    │  ┌──────────────────────────────────────────┐    │
    │  │ AskService (Question Answering)          │    │
    │  │ - Embedding Generation                   │    │
    │  │ - RAG Context Retrieval                  │    │
    │  │ - LLM Answer Generation                  │    │
    │  └──────────────────────────────────────────┘    │
    │  ┌──────────────────────────────────────────┐    │
    │  │ Support Services                         │    │
    │  │ - RagContextRetrievalService             │    │
    │  │ - PdfSemanticChunkingService             │    │
    │  └──────────────────────────────────────────┘    │
    └────────────────┬───────────────────┬─────────────┘
                     │                   │
            ┌────────▼────────┐   ┌──────▼─────────┐
            │   MySQL 8.0.36  │   │  Ollama Engine │
            │   (Port 3307)   │   │  (Port 11435)  │
            │  ┌────────────┐ │   │ ┌────────────┐ │
            │  │  documents │ │   │ │ tinyllama  │ │
            │  │  rag_chunks│ │   │ │ (Chat)     │ │
            │  └────────────┘ │   │ └────────────┘ │
            │                 │   │ ┌────────────┐ │
            │                 │   │ │nomic-embed │ │
            │                 │   │ │(Embedding) │ │
            │                 │   │ └────────────┘ │
            └─────────────────┘   └────────────────┘
```

## 🚀 Quick Start

### Prerequisites

- **Java 21** - Required for the application
- **Docker & Docker Compose** - For containerized services
- **Gradle** - Build automation (included via wrapper)

### Option 1: Full Automated Setup (Recommended)

```powershell
# Navigate to project directory
cd C:\Users\NagarjunaNallabothul\IdeaProjects\feed-ask-ai

# Complete setup: Start Docker → Clean → Generate OpenAPI → Build
.\gradlew.bat start
```

**This will:**
1. ✅ Verify Docker installation
2. ✅ Start MySQL and Ollama containers
3. ✅ Wait for services to be healthy (15 seconds)
4. ✅ Clean previous build artifacts
5. ✅ Generate API code from OpenAPI spec
6. ✅ Build the entire project
7. ✅ Display completion status

### Option 2: Step-by-Step Setup

```powershell
# 1. Start Docker containers
.\gradlew.bat initEnv

# 2. Verify models are available (models auto-pull on first use)
docker exec feed-ask-ai-ollama ollama list

# 3. Build the project
.\gradlew.bat build

# 4. Run the application
.\gradlew.bat bootRun
```

### Stopping the Application

```powershell
# Stop Docker containers and clean build
.\gradlew.bat stop

# OR just stop containers
.\gradlew.bat dockerComposeDown
```

## 📊 Available Gradle Tasks

| Task | Purpose | Example |
|------|---------|---------|
| `start` | Full setup & build | `.\gradlew.bat start` |
| `build` | Build project | `.\gradlew.bat build` |
| `bootRun` | Run application | `.\gradlew.bat bootRun` |
| `initEnv` | Start Docker containers | `.\gradlew.bat initEnv` |
| `dockerComposeUp` | Start Docker services | `.\gradlew.bat dockerComposeUp` |
| `dockerComposeDown` | Stop Docker services | `.\gradlew.bat dockerComposeDown` |
| `clean` | Clean build + stop Docker | `.\gradlew.bat clean` |
| `test` | Run tests | `.\gradlew.bat test` |

## 🌐 API Endpoints

### 1. Upload PDF Document

**Endpoint:** `POST /api/v1/feed-ask/feed`

**Content-Type:** `multipart/form-data`

**Request:**
```bash
curl -X POST \
  -F "file=@document.pdf" \
  http://localhost:8080/api/v1/feed-ask/feed
```

**Response (200 OK):**
```json
{
  "status": "SUCCESS",
  "message": "Document uploaded and processed successfully",
  "data": {
    "documentId": 1,
    "documentName": "document.pdf",
    "pageCount": 10,
    "embeddingsCount": 45,
    "processingTimeMs": 8450,
    "uploadedAt": "2024-05-06T10:30:00Z"
  }
}
```

**Status Codes:**
- `200 OK` - Document successfully processed
- `400 Bad Request` - Invalid PDF file
- `413 Payload Too Large` - File exceeds 100MB
- `500 Internal Server Error` - Processing error

### 2. Ask a Question

**Endpoint:** `GET /api/v1/feed-ask/ask`

**Query Parameters:**
- `prompt` (required, string): Your question

**Request:**
```bash
curl -X GET \
  "http://localhost:8080/api/v1/feed-ask/ask?prompt=What%20is%20the%20main%20topic"
```

**Response (200 OK):**
```json
{
  "status": "SUCCESS",
  "prompt": "What is the main topic?",
  "answer": "Based on the document content, the main topic discusses...",
  "referredFrom": [
    "document.pdf"
  ]
}
```

**Status Codes:**
- `200 OK` - Question answered successfully
- `400 Bad Request` - Missing or empty prompt
- `500 Internal Server Error` - Processing error

## 📚 Interactive API Documentation

Access the Swagger UI to explore and test endpoints:

```
http://localhost:8080/swagger-ui.html
```

Or view the OpenAPI specification:
```
http://localhost:8080/v3/api-docs
```

## ⚙️ Configuration

### Database Configuration

File: `src/main/resources/application.yaml`

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/feed_ask_ai?createDatabaseIfNotExist=true
    username: admin
    password: admin123
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### Ollama Configuration

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11435
      chat:
        model: tinyllama
      embedding:
        model: nomic-embed-text
```

### File Upload Limits

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB
```

### RAG Settings

```yaml
top:
  match:
    max-results: 3  # Top K results for vector search
```

## 🐳 Docker Services

### MySQL Service
- **Image:** `mysql:8.0.36`
- **Port:** `3307:3306`
- **Database:** `feed_ask_ai`
- **Credentials:** `admin` / `admin123`
- **Storage:** Persistent volume (`mysql_data`)
- **Health Check:** MySQL ping every 5 seconds

### Ollama Service
- **Image:** `ollama/ollama:latest`
- **Port:** `11435:11434`
- **Models:** `tinyllama` (5B), `nomic-embed-text` (137M)
- **Storage:** Persistent volume (`ollama_data`)
- **Health Check:** API taglist every 10 seconds
- **Auto-pull:** Models auto-download on first use

## 📊 Data Model

### Documents Table
```sql
CREATE TABLE documents (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  document_name VARCHAR(255) UNIQUE NOT NULL,
  page_count INT,
  chunks_count INT,
  processing_time_ms BIGINT,
  file_hash VARCHAR(500) UNIQUE,
  status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED'),
  uploaded_at DATETIME NOT NULL,
  updated_at DATETIME
);
```

### RAG Chunks Table
```sql
CREATE TABLE rag_chunks (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  document_id BIGINT NOT NULL,
  content LONGTEXT NOT NULL,
  page_number INT,
  embedding LONGTEXT,
  created_at DATETIME,
  updated_at DATETIME,
  FOREIGN KEY (document_id) REFERENCES documents(id)
);
```

## 🔄 Processing Workflow

### PDF Upload Workflow

```
1. User uploads PDF file
     ↓
2. Validate file (format, size, duplication)
     ↓
3. Create Document entity (PENDING status)
     ↓
4. Extract text from PDF
     ↓
5. Perform semantic chunking
     ↓
6. Update Document status → PROCESSING
     ↓
7. For each chunk:
     ├─ Generate embedding (nomic-embed-text)
     ├─ Store chunk with embedding
     └─ Update progress
     ↓
8. Update Document status → COMPLETED
     ↓
9. Return response with statistics
```

### Question Answering Workflow

```
1. User asks a question
     ↓
2. Generate embedding for question
     ↓
3. Retrieve all stored chunks
     ↓
4. Calculate cosine similarity scores
     ↓
5. Filter by threshold: similarity > 0.75
     ↓
6. Select top-K most similar chunks (K=3)
     ↓
7. Build context from selected chunks
     ↓
8. Send prompt to LLM:
     ├─ System: "Answer based on context"
     ├─ Context: Selected chunks
     └─ Question: User's query
     ↓
9. Generate answer (tinyllama)
     ↓
10. Return answer with source documents
```

## 🔍 Semantic Chunking

The system uses intelligent text splitting that:

- **Respects Structure**: Maintains paragraph boundaries
- **Handles Overlaps**: 100-200 token overlap between chunks
- **Token-Aware**: ~2000 character (~500 tokens) per chunk
- **Preserves Context**: Keeps semantic units together
- **Page Tracking**: Maintains original page numbers

## 📈 Vector Similarity Calculation

The system uses **Cosine Similarity** for finding relevant documents:

```
Similarity(A, B) = (A · B) / (||A|| × ||B||)

Where:
  A, B = embedding vectors
  · = dot product
  || || = L2 norm (magnitude)

Score Range:
  0.0 = completely different
  0.5 = moderately similar  
  1.0 = identical
```

**Filtering:** Only chunks with similarity > 0.75 are considered relevant

## 🔧 Troubleshooting

### Container Issues

**Check Docker containers status:**
```bash
docker ps -a
```

**View container logs:**
```bash
# MySQL logs
docker logs feed-ask-ai-mysql

# Ollama logs
docker logs feed-ask-ai-ollama
```

**Verify services are healthy:**
```bash
# MySQL health check
docker exec feed-ask-ai-mysql mysql -u admin -p admin123 -e "SELECT 1"

# Ollama health check
curl http://localhost:11435/api/tags
```

### Model Issues

**Check available models:**
```bash
docker exec feed-ask-ai-ollama ollama list
```

**Manual model pull:**
```bash
# Embedding model
docker exec feed-ask-ai-ollama ollama pull nomic-embed-text

# Chat model
docker exec feed-ask-ai-ollama ollama pull tinyllama
```

### Connection Issues

**MySQL Connection Error:**
- Check if MySQL is running: `docker exec feed-ask-ai-mysql ping -c 1`
- Verify credentials in `application.yaml`
- Check port: Should be 3307, not 3306

**Ollama Connection Error:**
- Check if Ollama is running: `curl http://localhost:11435/api/tags`
- Verify URL in `application.yaml`: Should be `http://localhost:11435`
- Check port: Should be 11435, not 11434

**Build Fails:**
- Clean and rebuild: `.\gradlew.bat clean build`
- Check Java version: `java -version` (must be 21+)
- Force dependency update: `.\gradlew.bat --refresh-dependencies build`

## 📦 Project Structure

```
feed-ask-ai/
├── src/main/
│   ├── java/com/rb/feed_ask_ai/
│   │   ├── controller/
│   │   │   └── FeedAskAiController.java
│   │   ├── service/
│   │   │   ├── FeedService.java              # Document upload handling
│   │   │   ├── AskService.java               # Q&A processing
│   │   │   ├── PdfSemanticChunkingService.java
│   │   │   └── RagContextRetrievalService.java
│   │   ├── entity/
│   │   │   ├── DocumentEntity.java
│   │   │   └── RagEntity.java
│   │   ├── repository/
│   │   │   ├── DocRepository.java
│   │   │   └── RagRepository.java
│   │   ├── model/                           # OpenAPI generated models
│   │   ├── api/                             # OpenAPI generated API
│   │   └── FeedAskAiApplication.java
│   └── resources/
│       ├── application.yaml                 # Configuration
│       └── openapi.yaml                     # API specification
├── build.gradle                             # Build configuration
├── docker-compose.yml                       # Container orchestration
├── gradlew / gradlew.bat                    # Gradle wrapper
└── README.md                                # This file
```

## 🚀 Key Technologies

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 21 | Programming language |
| Spring Boot | 4.0.6 | Web framework |
| Spring AI | 2.0.0-M5 | LLM integration |
| Spring Data JPA | Latest | Database ORM |
| MySQL | 8.0.36 | Database |
| Ollama | Latest | LLM engine |
| PDFBox | 3.0.1 | PDF processing |
| Apache Commons | 3.14.0 | Utilities |
| Lombok | Latest | Code generation |
| SpringDoc OpenAPI | 2.4.0 | API documentation |

## 📝 Logging

Application logs are configured at `DEBUG` level for `com.rb.feed_ask_ai`:

```
2024-05-06 10:30:37 - Servlet.service() for servlet [dispatcherServlet]
2024-05-06 10:30:37 - Starting Docker containers...
2024-05-06 10:30:52 - ✓ Docker containers started
```

**Log Levels:**
- `INFO`: Important application events
- `DEBUG`: Detailed processing information
- `ERROR`: Application errors

## 🔐 Security Considerations

- ✅ File validation (format, size, deduplication)
- ✅ Input sanitization for prompts
- ✅ CORS configuration ready
- ⚠️ TODO: API authentication
- ⚠️ TODO: Rate limiting
- ⚠️ TODO: HTTPS/TLS

## 📊 Performance Characteristics

| Operation | Typical Duration |
|-----------|-----------------|
| PDF Processing (10 pages) | 5-10 seconds |
| Embedding Generation (per chunk) | 100-500ms |
| Vector Search (100K chunks) | 50-200ms |
| LLM Answer Generation | 2-5 seconds |
| **Total Q&A Latency** | **3-8 seconds** |

## 🎯 Future Enhancements

- [ ] Add batch document upload
- [ ] Implement document deletion with cascade cleanup
- [ ] Add conversational chat history
- [ ] Multi-language support
- [ ] Hybrid search (semantic + keyword)
- [ ] Query result caching
- [ ] Advanced RAG scoring
- [ ] Multiple embedding model support
- [ ] API authentication & authorization
- [ ] Rate limiting
- [ ] Analytics dashboard
- [ ] Async processing for large files
- [ ] GraphQL API support

## 🤝 Contributing

This is a development project. For contributions:
1. Create a feature branch
2. Make your changes
3. Build with `.\gradlew.bat build`
4. Test thoroughly
5. Submit for review

## 📄 License

MIT License - See LICENSE file for details

## 📞 Support

For issues and questions:

1. **Check Logs**: Review application logs in console output
2. **Troubleshooting Guide**: See troubleshooting section above
3. **Docker Status**: Verify containers are running and healthy
4. **API Documentation**: Visit Swagger UI at http://localhost:8080/swagger-ui.html

## 🎓 Learn More

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring AI Reference](https://docs.spring.io/spring-ai/reference/)
- [Ollama Documentation](https://github.com/ollama/ollama)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [OpenAPI Specification](https://spec.openapis.org/oas/latest.html)

---

**Last Updated:** May 6, 2026  
**Current Version:** 0.0.1-SNAPSHOT
