package nro.core.loop;

/**
 * Đối tượng có thể được GameLoop tick theo chu kỳ.
 * Ví dụ: map, zone, boss-loop, dungeon-loop, phó bản...
 *
 * LƯU Ý:
 * - tick(long nowMillis) NÊN chạy nhanh, không block lâu.
 *   Nếu logic nặng (scan list lớn, AI phức tạp, I/O...), hãy đẩy sang GameScheduler.SCHED.execute(..)
 *   để tránh làm chậm GameLoop.
 *
 * - periodMs() là chu kỳ tick mong muốn (ms).
 *   GameLoop sẽ gọi tick() theo period này, nhưng vẫn có giới hạn ngân sách mỗi frame.
 *
 * - isActive() cho phép giảm tần số tick khi thực thể “rảnh” (vd: map không có player).
 */
public interface Tickable {

    /**
     * Chạy 1 frame logic cho thực thể.
     *
     * @param nowMillis thời gian hiện tại (System.currentTimeMillis()) do GameLoop truyền vào.
     * @throws Exception cho phép ném ra, GameLoop sẽ bắt và không làm crash server.
     */
    void tick(long nowMillis) throws Exception;

    /**
     * Chu kỳ tick mong muốn (ms).
     * Ví dụ:
     *  - Map: 200ms
     *  - Dungeon: 150–500ms
     *  - BossLoop: 200–500ms tùy số boss
     */
    int periodMs();

    /**
     * Thực thể có đang hoạt động hay không?
     * GameLoop có thể dùng thông tin này để giảm tần số tick
     * (vd: map không có player thì period tối thiểu 1000ms).
     */
    default boolean isActive() {
        return true;
    }
}
