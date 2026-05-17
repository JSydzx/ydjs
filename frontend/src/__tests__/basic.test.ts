import { describe, it, expect } from 'vitest'

describe('前端基础测试', () => {
  it('应该通过基本断言', () => {
    expect(true).toBe(true)
    expect(1 + 2).toBe(3)
    expect('hello').toContain('ll')
  })

  it('Array 方法测试', () => {
    const arr = [1, 2, 3, 4, 5]
    expect(arr.length).toBe(5)
    expect(arr.filter((n) => n > 2)).toEqual([3, 4, 5])
    expect(arr.map((n) => n * 2)).toEqual([2, 4, 6, 8, 10])
  })

  it('Object 方法测试', () => {
    const obj = { name: 'test', value: 123, active: true }
    expect(Object.keys(obj).length).toBe(3)
    expect(Object.values(obj)).toEqual(['test', 123, true])
  })

  it('String 方法测试', () => {
    const str = 'Hello World'
    expect(str.toLowerCase()).toBe('hello world')
    expect(str.split(' ')).toEqual(['Hello', 'World'])
  })
})
