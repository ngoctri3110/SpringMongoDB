# 🎯 Spring Boot + MongoDB - E-Commerce Learning Platform

Một **platform học tập toàn diện** từ cơ bản đến nâng cao về MongoDB + Spring Data MongoDB, với mục tiêu giúp developer đạt trình độ **Java Senior**.

---

## 📚 Học Tập

### Cách Sử Dụng Project

1. **Đọc docs** - Bắt đầu từ `docs/01-fundamentals/01-mongodb-basics.md`
2. **Xem code** - Examine corresponding Java files trong src/
3. **Thực hành** - Test API endpoints bằng curl hoặc Postman
4. **Viết test** - Tạo test cases cho từng feature
5. **Modify** - Thêm tính năng mới, experiment

### Phase Progression

| Phase | Thời Gian | Focus | Docs |
|-------|----------|-------|------|
| 1 | 1-2 tuần | CRUD Basics | `docs/01-fundamentals/` |
| 2 | 1 tuần | Query Methods | `docs/02-querying/` |
| 3 | 1-2 tuần | Relationships | `docs/03-relationships/` |
| 4 | 2 tuần | Aggregation | `docs/04-advanced-queries/` |
| 5 | 2 tuần | Indexing | `docs/05-indexing/` |
| 6 | 1 tuần | Transactions | `docs/07-transactions/` |
| 7 | 1-2 tuần | Production Ready | `docs/08-security-production/` |
| 8 | 2 tuần | Advanced Patterns | `docs/09-advanced-patterns/` |

### Mục Tiêu Học Tập

**Phase 1**: CRUD cơ bản
- Basic Create, Read, Update, Delete operations
- Document modeling
- Spring Data MongoDB setup

**Phase 2**: Query mastery
- Query methods
- MongoTemplate
- Pagination & sorting

**Phase 3**: Relationships & embedding
- Embedded documents
- Document references
- Denormalization strategies

**Phase 4**: Aggregation pipeline
- Aggregation framework
- Group, match, project stages
- Text search

**Phase 5**: Indexing & performance
- Index types & strategies
- Query optimization
- Performance monitoring

**Phase 6**: Transactions & consistency
- ACID transactions
- Multi-document transactions
- Optimistic locking

**Phase 7**: Production ready
- Audit logging
- Database migrations
- Security & encryption

**Phase 8**: Advanced patterns
- Event Sourcing
- CQRS pattern
- Saga pattern

---

## 📖 Documentation Structure

```
docs/
├── 01-fundamentals/            # Phase 1: Foundation
│   ├── 01-mongodb-basics.md
│   ├── 02-spring-data-mongodb-setup.md
│   ├── 03-document-modeling.md
│   └── 04-basic-crud.md
├── 02-querying/                # Phase 2: Query Mastery
├── 03-relationships/           # Phase 3: Relationships
├── 04-advanced-queries/        # Phase 4: Aggregation
├── 05-indexing/                # Phase 5: Indexing & Performance
├── 06-performance/
├── 07-transactions/            # Phase 6: Transactions
├── 08-security-production/     # Phase 7: Production
└── 09-advanced-patterns/       # Phase 8: Advanced
```

---

## 🎓 Learning Path

### Recommended Sequence

1. ✅ Read **01-mongodb-basics.md** - Understand document model
2. ✅ Read **02-spring-data-mongodb-setup.md** - Setup Spring Data
3. ✅ Read **03-document-modeling.md** - Design schemas
4. ✅ Read **04-basic-crud.md** - Basic operations
5. 📝 Run Phase 1 tests: `mvn test -Dtest=*Repository*Test`
6. 🔍 Explore Phase 2 queries
7. 🔗 Understand relationships (Phase 3)
8. ... and so on

---

## 📝 Notes

- **Tất cả documentation viết bằng Vietnamese** - Dễ hiểu cho người Việt
- **Code examples đầy đủ** - Có thể copy-paste chạy ngay
- **Real-world scenarios** - Các ví dụ thực tế từ e-commerce
- **Best practices** - Follow MongoDB & Spring Data best practices
- **Production ready** - Code patterns sử dụng được trong production

---

## 🎯 Next Steps

1. **Start Phase 1**: Go to `docs/01-fundamentals/01-mongodb-basics.md`
2. **Run Docker**: `docker-compose up -d`
3. **Verify**: `mvn clean compile`
4. **Test API**: Use Postman/curl to test endpoints
5. **Read Code**: Examine implementations in `src/main/java`
6. **Modify**: Try adding new features
7. **Progress**: Move to next phase when comfortable

---

**Happy Learning! 🚀**

*Last Updated: March 2026*
