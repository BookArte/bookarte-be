INSERT INTO book_monthly_stats (book_id, stat_year, stat_month, borrow_count)
SELECT
    book_id,
    YEAR(created_at) AS stat_year,
    MONTH(created_at) AS stat_month,
    COUNT(*) AS borrow_count
FROM borrow
WHERE created_at < DATE_FORMAT(CURDATE(), '%Y-%m-01')
GROUP BY book_id, YEAR(created_at), MONTH(created_at)
ON DUPLICATE KEY UPDATE borrow_count = VALUES(borrow_count);

COMMIT;