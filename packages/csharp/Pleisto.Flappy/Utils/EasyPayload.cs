namespace Pleisto.Flappy.Utils
{
    /// <summary>
    /// Generic payload type packer
    /// </summary>
    /// <typeparam name="T">call reference</typeparam>
    public class EasyPayload<T> : IEquatable<EasyPayload<T>>, IEquatable<T>
    {
        /// <summary>
        /// Payload
        /// </summary>
        public T Payload { get; set; }

        /// <summary>
        /// Equals
        /// </summary>
        /// <param name="other"></param>
        /// <returns></returns>
        public bool Equals(T other)
        {
            return Payload.Equals(other);
        }

        /// <summary>
        /// Equals
        /// </summary>
        /// <param name="other"></param>
        /// <returns></returns>
        public bool Equals(EasyPayload<T> other)
        {
            return Payload.Equals((T)other);
        }

        /// <summary>
        /// Fast convert to payload
        /// </summary>
        /// <param name="payload"></param>
        public static implicit operator EasyPayload<T>(T payload)
        {
            return new EasyPayload<T> { Payload = payload };
        }

        /// <summary>
        /// Fast convert from payload
        /// </summary>
        /// <param name="payload"></param>
        public static implicit operator T(EasyPayload<T> payload)
        {
            if (payload == null)
                return default;
            else
                return payload.Payload;
        }

    }
}
