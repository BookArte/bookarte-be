-- 카테고리 테이블
CREATE TABLE category (
                          category_id BIGINT NOT NULL AUTO_INCREMENT,
                          category_code VARCHAR(255) NOT NULL,
                          category_name VARCHAR(255),
                          PRIMARY KEY (category_id),
                          CONSTRAINT UK_category_code UNIQUE (category_code)
) ENGINE=InnoDB;

-- 회원 테이블
CREATE TABLE member (
                        member_id BIGINT NOT NULL AUTO_INCREMENT,
                        member_user_id VARCHAR(50) NOT NULL,
                        member_pwd VARCHAR(255),
                        member_name VARCHAR(50) NOT NULL,
                        member_email VARCHAR(255) NOT NULL,
                        member_tel VARCHAR(255) NOT NULL,
                        member_point BIGINT DEFAULT 0 NOT NULL,
                        member_role VARCHAR(15) NOT NULL,
                        member_status VARCHAR(15) NOT NULL,
                        member_social_type VARCHAR(15) NOT NULL,
                        member_social_key VARCHAR(255),
                        member_memo TEXT,
                        member_out_reason TEXT,
                        use_privacy_yn CHAR(1) DEFAULT 'N' NOT NULL,
                        use_service_yn CHAR(1) DEFAULT 'N' NOT NULL,
                        use_privacy_date DATETIME(6),
                        use_service_date DATETIME(6),
                        member_last_login DATETIME(6),
                        member_out_date DATETIME(6),
                        created_at DATETIME(6) NOT NULL,
                        updated_at DATETIME(6),
                        PRIMARY KEY (member_id),
                        CONSTRAINT UK_member_user_id UNIQUE (member_user_id)
) ENGINE=InnoDB;

-- 도서 테이블 (제목 FTS 인덱스 포함)
CREATE TABLE book (
                      book_id BIGINT NOT NULL AUTO_INCREMENT,
                      category_id BIGINT NOT NULL,
                      book_title VARCHAR(255) NOT NULL,
                      publisher_name VARCHAR(255) NOT NULL,
                      publication_date DATE NOT NULL,
                      book_isbn VARCHAR(255) NOT NULL,
                      book_contents TEXT NOT NULL,
                      book_call_number VARCHAR(255),
                      book_thumbnail VARCHAR(255) NOT NULL,
                      can_borrow BIT NOT NULL,
                      created_at DATETIME(6) NOT NULL,
                      updated_at DATETIME(6),
                      PRIMARY KEY (book_id),
                      CONSTRAINT FK_book_category FOREIGN KEY (category_id) REFERENCES category (category_id),
    -- 제목 검색을 위한 Full-Text Index 추가
                      FULLTEXT INDEX idx_fts_title (book_title) WITH PARSER ngram
) ENGINE=InnoDB;

-- 도서 참여자 테이블 (고도화 복합 인덱스 포함)
CREATE TABLE book_participant (
                                  book_id BIGINT NOT NULL,
                                  name VARCHAR(255) NOT NULL,
                                  type ENUM('AUTHOR', 'TRANSLATOR') NOT NULL,
                                  CONSTRAINT FK_participant_book FOREIGN KEY (book_id) REFERENCES book (book_id),
    -- 저자 검색 최적화를 위한 커버링 복합 인덱스
                                  INDEX idx_participant_search_covering (name, type, book_id)
) ENGINE=InnoDB;

-- 대출 테이블
CREATE TABLE borrow (
                        borrow_id BIGINT NOT NULL AUTO_INCREMENT,
                        book_id BIGINT NOT NULL,
                        member_id BIGINT NOT NULL,
                        status ENUM('BORROWED', 'OVERDUE', 'RETURNED', 'RETURN_REQUESTED'),
                        return_due_date DATE,
                        return_date DATE,
                        overdue_days INTEGER NOT NULL,
                        is_overdue BIT NOT NULL,
                        can_extend BIT NOT NULL,
                        created_at DATETIME(6) NOT NULL,
                        updated_at DATETIME(6),
                        PRIMARY KEY (borrow_id),
                        CONSTRAINT FK_borrow_book FOREIGN KEY (book_id) REFERENCES book (book_id),
                        CONSTRAINT FK_borrow_member FOREIGN KEY (member_id) REFERENCES member (member_id)
) ENGINE=InnoDB;

-- 게시판 테이블 (상속 구조 통합)
CREATE TABLE board (
                       board_id BIGINT NOT NULL AUTO_INCREMENT,
                       reg_member_id BIGINT NOT NULL,
                       mod_member_id BIGINT,
                       dtype VARCHAR(31) NOT NULL,
                       category VARCHAR(255),
                       title VARCHAR(255) NOT NULL,
                       contents LONGTEXT NOT NULL,
                       view_cnt BIGINT DEFAULT 0 NOT NULL,
                       order_num BIGINT DEFAULT 0 NOT NULL,
                       notice_yn CHAR(1) DEFAULT 'N' NOT NULL,
                       created_at DATETIME(6) NOT NULL,
                       updated_at DATETIME(6),
                       PRIMARY KEY (board_id),
                       CONSTRAINT FK_board_reg_member FOREIGN KEY (reg_member_id) REFERENCES member (member_id),
                       CONSTRAINT FK_board_mod_member FOREIGN KEY (mod_member_id) REFERENCES member (member_id),
                       CHECK (dtype IN ('NEWS', 'NOTICE'))
) ENGINE=InnoDB;

-- 공지사항/뉴스 상세 (1:1 식별 관계)
CREATE TABLE news (
                      board_id BIGINT NOT NULL,
                      PRIMARY KEY (board_id),
                      CONSTRAINT FK_news_board FOREIGN KEY (board_id) REFERENCES board (board_id)
) ENGINE=InnoDB;

CREATE TABLE notice (
                        board_id BIGINT NOT NULL,
                        PRIMARY KEY (board_id),
                        CONSTRAINT FK_notice_board FOREIGN KEY (board_id) REFERENCES board (board_id)
) ENGINE=InnoDB;

-- 도서 추천 테이블
CREATE TABLE recommendation (
                                recommendation_id BIGINT NOT NULL AUTO_INCREMENT,
                                book_id BIGINT NOT NULL,
                                recommend_type ENUM('ADMIN_PICK', 'MAIN', 'MONTHLY', 'WEEKLY') NOT NULL,
                                priority INTEGER NOT NULL,
                                start_date DATE NOT NULL,
                                end_date DATE NOT NULL,
                                comments VARCHAR(255),
                                created_at DATETIME(6) NOT NULL,
                                updated_at DATETIME(6),
                                PRIMARY KEY (recommendation_id),
                                CONSTRAINT FK_recommend_book FOREIGN KEY (book_id) REFERENCES book (book_id),
                                INDEX idx_recommend_period (start_date, end_date)
) ENGINE=InnoDB;

-- 도서 월별 통계 테이블
CREATE TABLE book_monthly_stats (
                                    stats_id BIGINT NOT NULL AUTO_INCREMENT,
                                    book_id BIGINT,
                                    stat_year INTEGER NOT NULL,
                                    stat_month INTEGER NOT NULL,
                                    borrow_count BIGINT NOT NULL,
                                    PRIMARY KEY (stats_id),
                                    INDEX idx_book_id_period (book_id, stat_year, stat_month)
) ENGINE=InnoDB;

-- 파일 업로드 테이블
CREATE TABLE upload_file (
                             file_id BIGINT NOT NULL AUTO_INCREMENT,
                             ref_id BIGINT NOT NULL,
                             ref_type VARCHAR(255) NOT NULL,
                             original_name VARCHAR(255) NOT NULL,
                             file_url VARCHAR(255) NOT NULL,
                             extension VARCHAR(255) NOT NULL,
                             file_size BIGINT,
                             file_role VARCHAR(255),
                             created_at DATETIME(6) NOT NULL,
                             updated_at DATETIME(6),
                             PRIMARY KEY (file_id)
) ENGINE=InnoDB;

-- 희망 도서/찜 테이블
CREATE TABLE wish (
                      wish_id BIGINT NOT NULL AUTO_INCREMENT,
                      book_id BIGINT,
                      member_id BIGINT,
                      created_at DATETIME(6) NOT NULL,
                      updated_at DATETIME(6),
                      PRIMARY KEY (wish_id),
                      CONSTRAINT FK_wish_book FOREIGN KEY (book_id) REFERENCES book (book_id),
                      CONSTRAINT FK_wish_member FOREIGN KEY (member_id) REFERENCES member (member_id)
) ENGINE=InnoDB;

-- 연체 패널티 테이블
CREATE TABLE penalty (
                         penalty_id BIGINT NOT NULL AUTO_INCREMENT,
                         member_id BIGINT,
                         borrow_id BIGINT,
                         penalty_reason VARCHAR(255),
                         start_date DATE,
                         end_date DATE,
                         is_released BIT NOT NULL,
                         released_at DATETIME(6),
                         released_by VARCHAR(255),
                         release_reason VARCHAR(255),
                         last_modified_by VARCHAR(255),
                         created_at DATETIME(6) NOT NULL,
                         updated_at DATETIME(6),
                         PRIMARY KEY (penalty_id),
                         CONSTRAINT FK_penalty_member FOREIGN KEY (member_id) REFERENCES member (member_id),
                         CONSTRAINT FK_penalty_borrow FOREIGN KEY (borrow_id) REFERENCES borrow (borrow_id)
) ENGINE=InnoDB;

