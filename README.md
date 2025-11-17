## 편식(가제) - Convention

1. 개발해야할 것을 미리 이슈에 등록
2. dev 기반으로 이슈 넘버 기반 브랜치 생성
- `git switch -c "feat/#issue" dev`
- `git switch -c "feat/#23" dev`
3. PR 올릴 때 dev에 머지가 될 수 있도록 설정
- PR 제목은 이슈 이름과 동일하게 한다.

---

## Service Ports
- edge
  - gateway :8760
  - discovery :8761
- service
  - auth-service :8080
  - member-service :8082
  - product-service :8083
  - crawler-service :8084
