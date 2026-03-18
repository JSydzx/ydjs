~~~```markdown
```markdown
```markdown
```mermaid
erDiagram
    USER {
        int id
        string username
        string password
    }
    TEAM {
        int id
        string name
        int leaderId
    }
    JOIN_REQUEST {
        int id
        int userId
        int teamId
        string status
    }

    USER ||--o{ TEAM : creates
    USER ||--o{ JOIN_REQUEST : submits
    TEAM ||--o{ JOIN_REQUEST : receives